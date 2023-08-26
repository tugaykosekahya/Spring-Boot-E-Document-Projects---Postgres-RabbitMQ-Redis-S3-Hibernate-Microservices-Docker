package tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.impl;

import co.elastic.apm.api.CaptureSpan;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.dao.BelgeDaoJpa;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.dao.QueueDao;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.dao.impl.BelgeDaoJpaImpl;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.message.Messages;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.object.request.BodyInput;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.object.response.PdfResponse;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.BelgeAkisService;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.PdfService;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.BelgeService;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.util.S3Util;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;
import tr.gov.gib.tahsilat.thsbaseobject.GibResponse;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thsexternal.RestTemplateService;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thsutils.DateUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Component
public class PdfServiceImpl implements PdfService {
    @Value("${objectstorage.s3BucketNamePrefix}")
    public String S3_BUCKET_NAME_PREFIX;
    @Value("${objectstorage.s3FileSeperator}")
    public String S3_FILE_SEPERATOR;
    @Value("${api.evdbelge.goruntuleme.server.url}")
    public String PDF_GORUNTULEME_SERVICE_URL;
    private static final String BELGE_KUYRUKTA = "0";
    private static final String BELGE_AKTARILDI = "1";
    private static final String BELGE_MAP = "belgePdf";
    private static final Gson gson = new Gson();
    private final BelgeService belgeService;
    private final S3Util s3Util;
    private final RestTemplateService restTemplateUtilityService;
    private final GibObjectMapper gibObjectMapper;
    private final QueueDao queueDao;
    private final BelgeDaoJpa belgeDaoJpa;
    private final BelgeAkisService belgeAkisService;
    private final GibLogger logger = GibLoggerFactory.getLogger();


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name = "readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    public PdfServiceImpl(BelgeService belgeService, S3Util s3Util, RestTemplateService restTemplateUtilityService, GibObjectMapper gibObjectMapper, QueueDao queueDao, BelgeDaoJpaImpl belgeDaoJpa, BelgeAkisService belgeAkisService) {
        this.belgeService = belgeService;
        this.s3Util = s3Util;
        this.restTemplateUtilityService = restTemplateUtilityService;
        this.gibObjectMapper = gibObjectMapper;
        this.queueDao = queueDao;
        this.belgeDaoJpa = belgeDaoJpa;
        this.belgeAkisService = belgeAkisService;
    }

    @Override
    @CaptureSpan(value = "pdfOlustur", type = "pdfOlustur")
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public byte[] pdfOlustur(CreateBelgeInput createBelgeInput) throws GibException {
        BodyInput bodyInput = new BodyInput();
        bodyInput.setBody(createBelgeInput);
        ResponseEntity<GibResponse> response;

        createBelgeInput.setDurum(101);
        belgeGuncelle(createBelgeInput);
        belgeAkisService.akisGuncelle(createBelgeInput.getBelgeid());

        response = this.restTemplateUtilityService
                .exchangeAll(createBelgeInput, GibResponse.class, PDF_GORUNTULEME_SERVICE_URL, "pdfOlustur");
//                .exchangeAll(createBelgeInput, GibResponse.class, "http://ingresstest1.gib.gov.tr:32169/evdb/evdbelge/goruntuleme-server/", "pdfOlustur");
        logger.info(response.getBody().toString());
        Map data = (Map) response.getBody().getData();
        PdfResponse pdfResponse = gibObjectMapper.convertValue(data, PdfResponse.class);

        kuyrukVeRediseAt(createBelgeInput);

        writeToObjectStorage(createBelgeInput, pdfResponse.getPdfFile());

        return pdfResponse.getPdfFile();
    }

    public void belgeGuncelle(CreateBelgeInput createBelgeInput) throws GibException {
        try {
            belgeDaoJpa.belgeUpdate(createBelgeInput.getBelgeid(), createBelgeInput.getDurum());
        } catch (Exception e) {
            logger.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu"), e);
            throw new GibException(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu"), "");
        }
    }

    public void kuyrukVeRediseAt(CreateBelgeInput createBelgeInput) throws GibException {
        String key = createBelgeInput.getOrgOid() + ";" + createBelgeInput.getBelgeTuru() + ";" + createBelgeInput.getBelgeNo();
//        if (hasBelgeInRedis(key))
//            return;
        pushPdfQueue(createBelgeInput);
        JsonObject jsonRedis = new JsonObject();
        jsonRedis.addProperty("durum", "101");
        jsonRedis.addProperty("optime", DateUtils.getDateTime());
        putBelgeToRedis(key, jsonRedis.toString());
    }

    public boolean hasBelgeInRedis(String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.hasKey(BELGE_MAP, key);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(e.getMessage()), "");
        }
    }

    public void putBelgeToRedis(String key, String json) throws GibRemoteException {
        try {
            hashOperations.put(BELGE_MAP, key, json);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(e.getMessage()), "");
        }
    }

    public void pushPdfQueue(CreateBelgeInput createBelgeInput) throws GibRemoteException {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("belgeid", createBelgeInput.getBelgeid());
            json.addProperty("orgoid", createBelgeInput.getOrgOid());
            json.addProperty("belgeturu", createBelgeInput.getBelgeTuru());
            json.addProperty("belgeno", createBelgeInput.getBelgeNo());
            queueDao.pushPdfImzaliQueue(json);
        } catch (Exception e) {
            logger.error(Messages.KUYRUK_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.KUYRUK_ERISIM_HATASI.message(e.getMessage()), "");
        }
    }


    public String createFileName(String key) {
        return key + S3_FILE_SEPERATOR + "metadata";
    }

    public String createFileNamePdf(String key) {
        return key + ".pdf";
    }

    public String createFileNameSignedPdf(String key) {
        return key + S3_FILE_SEPERATOR + "imzali.pdf";
    }

    public String createKey(CreateBelgeInput createBelgeInput) {
        return createBelgeInput.getOrgOid() + S3_FILE_SEPERATOR + createBelgeInput.getBelgeTuru() + S3_FILE_SEPERATOR + createBelgeInput.getBelgeNo();
    }

    public String createBucketName(String belgeNo) {
        return S3_BUCKET_NAME_PREFIX + belgeNo.substring(0,4);
    }

    public void writeToObjectStorage(CreateBelgeInput createBelgeInput, byte[] report) throws GibException {
        HashMap<String, String> map = new HashMap<>();
        map.put("bucketName", createBucketName(createBelgeInput.getBelgeNo()));
        map.put("fileName", createFileNamePdf(createKey(createBelgeInput)));
        try {
            s3Util.dosyaYukleS3(map, report);
        } catch (GibRemoteException e) {
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        } catch (Exception e) {
            logger.error(Messages.S3_HATA.message(e.getMessage()), e);
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        }
    }




    public JsonObject getBelgeToRedis(String key) throws GibRemoteException {
        try {
            String result = readOnlyHashOperations.get(BELGE_MAP, key);
            if (StringUtils.isNotBlank(result))
                return gson.fromJson(result, JsonObject.class);
            else
                return new JsonObject();
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public String createFileName(Belge belge) {
        return belge.getOrgoid() + S3_FILE_SEPERATOR + belge.getBelgeturu() + S3_FILE_SEPERATOR + belge.getBelgeno() + S3_FILE_SEPERATOR + "metadata";
    }
}
