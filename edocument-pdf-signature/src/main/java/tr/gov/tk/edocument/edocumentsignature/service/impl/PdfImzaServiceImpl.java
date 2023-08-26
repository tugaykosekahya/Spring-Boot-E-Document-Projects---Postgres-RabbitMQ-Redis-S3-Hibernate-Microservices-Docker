package tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.dao.BelgeDaoJpa;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.BelgeAkisService;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.PdfImzaService;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.S3Service;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.util.S3Util;
import tr.gov.gib.signserver.client.GIBSSClient;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.util.signer.SignerConsts;
import tr.gov.gib.util.signer.SignerUtil;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;

@Component
public class PdfImzaServiceImpl implements PdfImzaService {
    @Value("${objectstorage.s3BucketNamePrefix}")
    public String S3_BUCKET_NAME_PREFIX;
    @Value("${objectstorage.s3FileSeperator}")
    public String S3_FILE_SEPERATOR;
    @Value("${signer.server.certId}")
    public String SIGN_SERVER_CERT_ID;
    @Value("${signer.server.url}")
    public String SIGN_SERVER_URL;
    @Value("${signer.server.clientId}")
    public String SIGN_SERVER_CLIENT_ID;
    @Value("${signer.server.algorithm}")
    public String SIGN_SERVER_ALGORITHM;
    @Value("${signer.zamanDamgasi.url}")
    public String ZAMAN_DAMGASI_URL;
    @Value("${signer.zamanDamgasi.user}")
    public Integer ZAMAN_DAMGASI_USER;
    @Value("${signer.zamanDamgasi.password}")
    public String ZAMAN_DAMGASI_PASSWORD;
    private static final String BELGE_MAP = "belgePdf";
    private static final Gson GSON = new Gson();
    private final BelgeDaoJpa belgeDaoJpa;
    private final BelgeAkisService belgeAkisService;
    private final S3Util s3Util;
    private final GibLogger logger = GibLoggerFactory.getLogger();

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name = "readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    public PdfImzaServiceImpl(BelgeDaoJpa belgeDaoJpa, BelgeAkisService belgeAkisService, S3Util s3Util) {
        this.belgeAkisService = belgeAkisService;
        this.belgeDaoJpa = belgeDaoJpa;
        this.s3Util = s3Util;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public byte[] pdfImzala(CreateBelgeInput createBelgeInput) throws GibException {
        createBelgeInput.setDurum(102);
        belgeGuncelle(createBelgeInput);
        belgeAkisService.akisGuncelle(createBelgeInput.getBelgeid());

        deleteBelgeFromRedis(createBelgeInput.getOrgOid() + ";" + createBelgeInput.getBelgeTuru() + ";" + createBelgeInput.getBelgeNo());

        byte[] signedMuhur = imzala(createBelgeInput);

        writeToObjectStorage(createBelgeInput, signedMuhur);

        return signedMuhur;

    }
    private static long copyStreamAndClose(InputStream in, OutputStream out) throws Exception {
        try {
            byte[] buf = new byte['耀'];
            long b = 0L;

            int len;
            while((len = in.read(buf)) != -1) {
                b += (long)len;
                out.write(buf, 0, len);
            }

            long var6 = b;
            return var6;
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }

                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception var14) {
                if (var14.getClass().getName().indexOf("ClientAbortException") > -1) {
                    System.err.println("ERROR>>>DysFileManager->out.close()->" + var14.getCause().toString());
                } else {
                    var14.printStackTrace();
                }
            }

        }
    }


    private byte[] imzala(CreateBelgeInput createBelgeInput) throws GibRemoteException {
        File outputFile = null;
        File signedFile = null;
        String key = createKey(createBelgeInput);

        try {
            GIBSSClient.setProperties(SIGN_SERVER_URL,
                    SIGN_SERVER_CLIENT_ID, SIGN_SERVER_ALGORITHM);
            SignerUtil.setTSSettings(ZAMAN_DAMGASI_URL, ZAMAN_DAMGASI_USER, ZAMAN_DAMGASI_PASSWORD, DigestAlg.SHA256);

            byte[] report = belgeBul(createFileNamePdf(key), createBelgeInput.getBelgeNo());
//            if (report == null)


            outputFile = File.createTempFile(key, ".pdf");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(report);
            }

            signedFile = File.createTempFile(key, "_imzali.pdf");
            byte[] signedMuhur = GIBSSClient.getInstance().signFile(SIGN_SERVER_CERT_ID, outputFile,SignerConsts.TYPE_ESXLONG);
            try (FileOutputStream fos = new FileOutputStream(signedFile)) {
                fos.write(signedMuhur);
            }

            return signedMuhur;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GibRemoteException(Messages.IMZA_HATA.message(e.getMessage()), "");
        } finally {
            if (outputFile != null && outputFile.exists()) {
                outputFile.delete();
            }
            if (signedFile != null && signedFile.exists()) {
                signedFile.delete();
            }
        }
    }

    public byte[] readByte(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public byte[] belgeBul(String key, String belgeNo) throws GibRemoteException {
        try {
            InputStream inputStream = s3Util.dosyaInputStream(createBucketName(belgeNo), key);
            return readByte(inputStream);
        } catch (Exception e) {
            logger.error(Messages.S3_HATA.message(e.getMessage()), e);
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        }
    }

    public void belgeGuncelle(CreateBelgeInput createBelgeInput) throws GibException {
        try {
            belgeDaoJpa.belgeUpdate(createBelgeInput.getBelgeid(), createBelgeInput.getDurum());
        } catch (Exception e) {
            logger.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu"), e);
            throw new GibException(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu"), "");
        }
    }

    public String createFileNamePdf(String key) {
        return key + ".pdf";
    }

    public String createFileNameSignedPdf(String key) {
        return key + S3_FILE_SEPERATOR + "imzali.pdf";
    }

    public String createBucketName(String belgeNo) {
        return S3_BUCKET_NAME_PREFIX + belgeNo.substring(0,4);
    }

    public String createKey(CreateBelgeInput createBelgeInput) {
        return createBelgeInput.getOrgOid() + S3_FILE_SEPERATOR + createBelgeInput.getBelgeTuru() + S3_FILE_SEPERATOR + createBelgeInput.getBelgeNo();
    }

    public boolean hasBelgeInRedis(String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.hasKey(BELGE_MAP, key);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public void deleteBelgeFromRedis(String key) throws GibRemoteException {
        try {
            hashOperations.delete(BELGE_MAP, key);
        } catch (Exception e) {
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public void putBelgeToRedis(String key, String json) throws GibRemoteException {
        try {
            hashOperations.put(BELGE_MAP, key, json);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public JsonObject getBelgeToRedis(String key) throws GibRemoteException {
        try {
            String result = readOnlyHashOperations.get(BELGE_MAP, key);
            if (StringUtils.isNotBlank(result))
                return GSON.fromJson(result, JsonObject.class);
            else
                return new JsonObject();
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public void writeToObjectStorage(CreateBelgeInput createBelgeInput, byte[] report) throws GibException {
        HashMap<String, String> map = new HashMap<>();
        map.put("bucketName", createBucketName(createBelgeInput.getBelgeNo()));
        map.put("fileName", createFileNameSignedPdf(createKey(createBelgeInput)));
        try {
            s3Util.dosyaYukleS3(map, report);
        } catch (GibRemoteException e) {
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        } catch (Exception e) {
            logger.error(Messages.S3_HATA.message(e.getMessage()), e);
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        }
    }

    public String createFileName(Belge belge) {
        return belge.getOrgoid() + S3_FILE_SEPERATOR + belge.getBelgeturu() + S3_FILE_SEPERATOR + belge.getBelgeno() + S3_FILE_SEPERATOR + "metadata";
    }

}
