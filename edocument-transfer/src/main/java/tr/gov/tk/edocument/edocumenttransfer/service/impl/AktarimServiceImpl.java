package tr.gov.gib.evdbelge.evdbelgeaktarma.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeaktarma.external.service.YevdoExternalService;
import tr.gov.gib.evdbelge.evdbelgeaktarma.message.Messages;
import tr.gov.gib.evdbelge.evdbelgeaktarma.service.AktarimService;
import tr.gov.gib.evdbelge.evdbelgeaktarma.service.BelgeService;
import tr.gov.gib.evdbelge.evdbelgeaktarma.service.S3Service;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thsutils.DateUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

@Component
public class AktarimServiceImpl implements AktarimService {
    private static final String BELGE_KUYRUKTA = "0";
    private static final String BELGE_AKTARILDI = "1";
    private static final String BELGE_MAP = "belgeIsleme";
    private static final Gson GSON = new Gson();
    private final BelgeService belgeService;
    private final S3Service s3Service;
    private final YevdoExternalService yevdoExternalService;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    private final GibLogger LOGGER_LOG = GibLoggerFactory.getLogger("serverLogFileLogger");
    @Value("${objectstorage.s3FileSeperator}") public String S3_FILE_SEPERATOR;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name="readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    public AktarimServiceImpl(BelgeService belgeService, S3Service s3Service, YevdoExternalService yevdoExternalService) {
        this.belgeService = belgeService;
        this.s3Service = s3Service;
        this.yevdoExternalService = yevdoExternalService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void belgeAktar(JsonObject jsonObject, Belge belge, String belgeIslemeKey) throws GibException {
        JsonObject json = getBelgeFromRedis(belgeIslemeKey);
        if(json.size() == 0 && belge.getDurum() == 1) {
            if(belgeService.belgeVarMi(belge.getBelgeno(), belge.getOrgoid(), belge.getBelgeturu())) {
                json.addProperty("status", BELGE_AKTARILDI);
                json.addProperty("optime", DateUtils.getDateTime());
                putBelgeToRedis(belgeIslemeKey, json.toString());
            } else {
                LOGGER.error("Belge rediste ve vtde yok.");
                throw new GibBusinessException("Belge rediste ve vtde yok.", "");
            }
        }
        else if(json.get("status").getAsString().equals(BELGE_KUYRUKTA)) {
            if(belge.getDurum() == 2) {
                Belge belgeDB = belgeService.belgeGetir(belge.getBelgeno(), belge.getOrgoid(), belge.getBelgeturu());
                if(belgeDB != null) {
                    belgeDB.setDurum((short) -1); //belgeyi iptal et
                    belgeDB.setKullanicikodu(belge.getKullanicikodu());
                    belgeDB.setNextnodeuserid(belge.getNextnodeuserid());
                }
                belgeService.belgeKaydet(belgeDB);
                json.addProperty("status", BELGE_AKTARILDI);
                putBelgeToRedis(belgeIslemeKey, json.toString());
            } else {
                belgeService.belgeKaydet(belge);
                json.addProperty("status", BELGE_AKTARILDI);
                putBelgeToRedis(belgeIslemeKey, json.toString());
                writeToObjectStorage(belge, jsonObject);
            }
        }
    }

    public void islenmisBelgeBildir(Belge belge, String belgeIslemeKey) throws GibException {
        JsonObject outJson = yevdoExternalService.islenmisBelgeBildir(belge);
        LOGGER_LOG.info("IslenecekVeriSonuc: " + outJson.toString());
        deleteBelgeFromRedis(belgeIslemeKey);
    }

    public boolean hasBelgeInRedis(String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.hasKey(BELGE_MAP, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }

    public void deleteBelgeFromRedis(String key) throws GibRemoteException {
        try {
            hashOperations.delete(BELGE_MAP, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }

    public void putBelgeToRedis(String key, String json) throws GibRemoteException {
        try {
            hashOperations.put(BELGE_MAP, key, json);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }

    public JsonObject getBelgeFromRedis(String key) throws GibRemoteException {
        try {
            String result = readOnlyHashOperations.get(BELGE_MAP, key);
            if(StringUtils.isNotBlank(result))
                return GSON.fromJson(result, JsonObject.class);
            else
                return new JsonObject();
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }

    public void writeToObjectStorage(Belge belge, JsonObject jsonObject) throws GibException {
        HashMap<String,String> map = new HashMap<>();
        map.put("optime", jsonObject.getAsJsonObject("jsonData").get("belgeno").getAsString().substring(0,4));
        map.put("fileName", createFileName(belge));
        String str = jsonObject.getAsJsonObject("jsonData").getAsJsonObject("response").toString();
        InputStream is = new ByteArrayInputStream(str.getBytes());
        try {
            s3Service.uploadToObjectStorage(map, is);
        } catch (GibRemoteException e) {
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()),"");
        } catch (Exception e) {
            LOGGER.error(Messages.S3_HATA.message(e.getMessage()), e);
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()),"");
        }
    }

    public String createFileName(Belge belge) {
        return belge.getOrgoid() + S3_FILE_SEPERATOR + belge.getBelgeturu() + S3_FILE_SEPERATOR + belge.getBelgeno() + S3_FILE_SEPERATOR + "metadata";
    }
}
