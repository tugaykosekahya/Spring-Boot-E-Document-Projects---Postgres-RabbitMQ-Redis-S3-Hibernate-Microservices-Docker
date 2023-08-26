package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.impl;

import co.elastic.apm.api.CaptureSpan;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.config.LivenessEventListener;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.message.Messages;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.ReportJson;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.GoruntulemeService;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.RedisService;
//import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.S3Service;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.tripledes.TripleDes;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.util.S3Util;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
public class GoruntulemeServiceImpl implements GoruntulemeService {

    @Value("${objectstorage.s3BucketNamePrefix}")
    public String S3_BUCKET_NAME_PREFIX;
    @Value("${objectstorage.s3FileSeperator}")
    public String S3_FILE_SEPERATOR;

    private static final Logger logger = LoggerFactory.getLogger(LivenessEventListener.class);
    private final Gson gson = new Gson();

    //    private final S3Service s3Service;
    private final RedisService redisService;
    private final TripleDes tripleDes;
    private final S3Util s3Util;
    private final GibObjectMapper gibObjectMapper;


    public GoruntulemeServiceImpl(/*S3Service s3Service,*/ RedisService redisService, TripleDes tripleDes, S3Util s3Util, GibObjectMapper gibObjectMapper) {
//        this.s3Service = s3Service;
        this.redisService = redisService;
        this.tripleDes = tripleDes;
        this.s3Util = s3Util;
        this.gibObjectMapper = gibObjectMapper;
    }

    @Override
    @CaptureSpan(value = "pdfGoruntule", type = "pdfGoruntule")
    public byte[] pdfGoruntule(String session, HttpServletResponse response) {
        byte[] report = new byte[0];
        String key = null;
        String raporAdi = null;
        try {
            key = tripleDes.decrypt(session);
            sessionControl(key);
            redisService.deleteSessionFromRedis(key);
            CreateBelgeInput createBelgeInput = convertKeyToCreateBelgeInput(key);
            key = createKey(createBelgeInput);
            ReportJson reportJson = raporDatasiniGetir(createBelgeInput, key);
            raporAdi = reportJson.getPdfSablon();


            report = exportReport(createBelgeInput, reportJson);
            if (createBelgeInput.getTaslak() != null && createBelgeInput.getTaslak().equals("1"))
                report = taslakEkle(report, reportJson);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            try {
                raporAdi = "belgeYuklenemedi";
                report = belgeYuklenemedi();
                redisService.deleteSessionFromRedis(key);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

        }
        response.setHeader("Content-Disposition", "inline; filename=" + raporAdi + ".pdf");
        response.setContentType("application/pdf");
        return report;
    }

    private byte[] taslakEkle(byte[] report, ReportJson reportJson) throws GibRemoteException {
        try {
            PdfReader reader = new PdfReader(report);
            File tempFile = File.createTempFile("taslakTemp", ".pdf");
            FileOutputStream outStream = new FileOutputStream(tempFile);
            PdfStamper stamper = new PdfStamper(reader, outStream);
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA,
                    "ISO-8859-9",
                    BaseFont.EMBEDDED);
            Font f = new Font(baseFont);
            f.setColor(Color.ORANGE);
            f.setStyle(-1);
            f.setSize(29);
            Phrase p = new Phrase("BU EVRAKIN E-İMZA İŞLEMİ HENÜZ TAMAMLANMAMIŞTIR", f);
            float x, y;
            com.lowagie.text.Rectangle pagesize;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                PdfContentByte over = stamper.getOverContent(i);
                over.saveState();
                PdfGState gs1 = new PdfGState();
                gs1.setFillOpacity(0.5f);
                over.setGState(gs1);
                pagesize = reader.getPageSizeWithRotation(i);
                x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                y = (pagesize.getTop() + pagesize.getBottom()) / 2;
                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 45);
                if (i == 1 && ObjectUtils.isNotEmpty(reportJson.getPdfSablon()) && reportJson.getPdfSablon().equals("odemeEmriSablon")) {
                    Font f1 = new Font(baseFont);
                    f1.setSize(30);
                    f1.setStyle(-1);
                    f1.setColor(Color.BLACK);
                    Phrase p1 = new Phrase("Ödeme Yaptıysanız Dikkate Almayiniz", f1);
                    ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p1, x, y, 0);
                }
                over.restoreState();
            }

            stamper.close();
            reader.close();
            outStream.close();

            FileInputStream fileInputStream = new FileInputStream(tempFile);
            report = readByte(fileInputStream);

            fileInputStream.close();

            return report;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GibRemoteException(Messages.TASLAK_EKLENEMEDI.message(), "");
        }
    }

    @Override
    @CaptureSpan(value = "pdfOlustur", type = "pdfOlustur")
    public byte[] pdfOlustur(CreateBelgeInput body) {
        byte[] report;
        String key;
        try {
            key = createKey(body);
            ReportJson reportJson = raporDatasiniGetir(body, key);
            report = exportReport(body, reportJson);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            report = null;
        }
        return report;
    }

    public byte[] belgeBul(CreateBelgeInput createBelgeInput, String key) {
        try {
            InputStream inputStream = s3Util.dosyaInputStream(createBucketName(createBelgeInput.getBelgeNo()), key);
            return readByte(inputStream);
        } catch (Exception e) {
            logger.error(Messages.S3_HATA.message(e.getMessage()), e);
            return null;
        }
    }

    public void sessionControl(String key) throws GibRemoteException {
        if (!redisService.hasSessionInRedis(key))
            throw new GibRemoteException(Messages.REDIS_SESSION_BULUNAMADI.message(), "");
    }

    private byte[] belgeYuklenemedi() throws GibRemoteException, JRException {
        InputStream resourceAsStream = jasperDosyasiniGetir(null, "belgeYuklenemedi.jasper");

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(resourceAsStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }


    @Override
    public byte[] exportReport(String key) throws GibRemoteException, JRException {
        CreateBelgeInput createBelgeInput = convertKeyToCreateBelgeInput(key);
        ReportJson reportJson = raporDatasiniGetir(createBelgeInput, key);
        return exportReport(createBelgeInput, reportJson);
    }

    public byte[] exportReport(CreateBelgeInput createBelgeInput, ReportJson reportJson) throws JRException, GibRemoteException {
        InputStream resourceAsStream = jasperDosyasiniGetir(createBelgeInput,reportJson.getPdfSablon() + ".jasper");

        Map<String, Object> detayGrupMap = raporDetaylariniGrupla(reportJson.getReportDetail());
        Map<String, Object> parameters = parametreleriGetir((List<String>) detayGrupMap.get("parameterList"), reportJson);
        Map<String, List<Map<String, Object>>> collectionListmap = collectionGetir((Map<String, Set<String>>) detayGrupMap.get("collectionMap"), reportJson);
        Map<String, List<Map<String, Object>>> dataSourceListmap = collectionGetir((Map<String, Set<String>>) detayGrupMap.get("dataSourceMap"), reportJson);

        JRBeanCollectionDataSource dataSourceOnly = null;
        if (dataSourceListmap.size() > 0) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : dataSourceListmap.entrySet()) {
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(entry.getValue());
                parameters.put(entry.getKey(), dataSource);
            }
        }
        if (collectionListmap.size() > 0) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : collectionListmap.entrySet()) {
                dataSourceOnly = new JRBeanCollectionDataSource(entry.getValue());
            }
        }

        if (createBelgeInput != null) {
            if (createBelgeInput.getMemur() != null) {
                parameters.put("memur", createBelgeInput.getMemur());
            }

            if (createBelgeInput.getSef() != null) {
                parameters.put("sef", createBelgeInput.getSef());
            }

            if (createBelgeInput.getMuduryrd() != null) {
                parameters.put("muduryrd", createBelgeInput.getMuduryrd());
            }

            if (createBelgeInput.getMudur() != null) {
                parameters.put("mudur", createBelgeInput.getMudur());
            }
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(resourceAsStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceOnly != null ? dataSourceOnly : new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf(jasperPrint);

    }

    private Map<String, List<Map<String, Object>>> collectionGetir(Map<String, Set<String>> collectionMap, ReportJson reportJson) {
        Map<String, List<Map<String, Object>>> collectionListmap = new HashMap<>();
        HashMap<String, Object> reportDetail = reportJson.getReportDetail();
        JsonObject reportData = reportJson.getReportData();

        for (Map.Entry<String, Set<String>> entry : collectionMap.entrySet()) {
            List<Map<String, Object>> collectionMapList = new ArrayList<>();
            JsonArray jsonArray = reportData.get(entry.getKey()).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonData = jsonArray.get(i).getAsJsonObject();
                Map<String, Object> collectionMaps = new HashMap<>();
                for (String field : entry.getValue()) {
                    degeriDonusturVeEkle(collectionMaps, jsonData, reportDetail, field);
                }
                collectionMapList.add(collectionMaps);
            }
            collectionListmap.put(entry.getKey(), collectionMapList);
        }
        return collectionListmap;
    }

    private Map<String, Object> parametreleriGetir(List<String> parameterList, ReportJson reportJson) {
        HashMap<String, Object> reportDetail = reportJson.getReportDetail();
        JsonObject reportData = reportJson.getReportData();

        Map<String, Object> parameterMap = new HashMap<>();
        for (String parameter : parameterList) {
            degeriDonusturVeEkle(parameterMap, reportData, reportDetail, parameter);
        }
        return parameterMap;
    }

    private InputStream jasperDosyasiniGetir(CreateBelgeInput createBelgeInput, String path) throws GibRemoteException {
        InputStream inputStream;
        String bucketName = createBelgeInput != null ? createBucketName(createBelgeInput.getBelgeNo()) : createBucketName();
        try {
            inputStream = s3Util.dosyaInputStream(bucketName, path);
        } catch (Exception e) {
            logger.error(Messages.S3_HATA.message(e.getMessage()), e);
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        }

        return inputStream;
    }

    private ReportJson raporDatasiniGetir(CreateBelgeInput createBelgeInput,String key) throws GibRemoteException {
        InputStream inputStream;
        try {
            inputStream = s3Util.dosyaInputStream(createBucketName(createBelgeInput.getBelgeNo()), createFileName(key));
            String jsonString = readString(inputStream);
            return gson.fromJson(jsonString, ReportJson.class);
        } catch (Exception e) {
            logger.error(Messages.S3_HATA.message(e.getMessage()), e);
            throw new GibRemoteException(Messages.S3_HATA.message(e.getMessage()), "");
        }
    }

    private String readString(InputStream inputStream) throws IOException {

        ByteArrayOutputStream into = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for (int n; 0 < (n = inputStream.read(buf)); ) {
            into.write(buf, 0, n);
        }
        into.close();
        return new String(into.toByteArray(), StandardCharsets.UTF_8.name());
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

    private void degeriDonusturVeEkle(Map<String, Object> map, JsonObject reportData, HashMap<String, Object> reportDetail, String detayAdi) {
        String datayDegeri = (String) reportDetail.get(detayAdi);
        String[] detayDegerArray = datayDegeri.split(",");

        logger.info("donusturulecek json: " + reportData.toString());

        switch (detayDegerArray[0]) {
            case "String":
                map.put(detayAdi, reportData.get(detayAdi).getAsString());
                break;
            case "Integer":
                map.put(detayAdi, reportData.get(detayAdi).getAsInt());
                break;
            case "Long":
                map.put(detayAdi, reportData.get(detayAdi).getAsLong());
                break;
            case "BigDecimal":
                map.put(detayAdi, reportData.get(detayAdi).getAsBigDecimal());
                break;
            default:
                break;
        }
    }

    private Map<String, Object> raporDetaylariniGrupla(HashMap<String, Object> reportDetail) {
        List<String> parameterList = new ArrayList<>();
        Map<String, Set<String>> collectionMap = new HashMap<>();
        Map<String, Set<String>> dataSourceMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : reportDetail.entrySet()) {
            String detayAdi = entry.getKey();
            String datayDegeri = (String) entry.getValue();
            String[] detayDegerArray = datayDegeri.split(",");

            switch (detayDegerArray[1]) {
                case "Parameter":
                    parameterList.add(detayAdi);
                    break;
                case "Collection":
                    if (!collectionMap.containsKey(detayAdi))
                        collectionMap.put(detayAdi, new HashSet<>());
                    break;
                case "DataSource":
                    if (!dataSourceMap.containsKey(detayAdi))
                        dataSourceMap.put(detayAdi, new HashSet<>());
                    break;
                case "Field":
                    String collectionName = detayDegerArray[2];
                    Set<String> fieldSet = collectionMap.get(collectionName) == null ? new HashSet<>() : collectionMap.get(collectionName);
                    fieldSet.add(detayAdi);
                    collectionMap.put(collectionName, fieldSet);
                    break;
                case "DataSourceField":
                    String dataSourceName = detayDegerArray[2];
                    Set<String> dataSourceFieldSet = dataSourceMap.get(dataSourceName) == null ? new HashSet<>() : dataSourceMap.get(dataSourceName);
                    dataSourceFieldSet.add(detayAdi);
                    dataSourceMap.put(dataSourceName, dataSourceFieldSet);
                    break;
                default:
                    break;
            }
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("parameterList", parameterList);
        returnMap.put("collectionMap", collectionMap);
        returnMap.put("dataSourceMap", dataSourceMap);

        return returnMap;
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

    public CreateBelgeInput convertKeyToCreateBelgeInput(String key) {
        String[] keyArray = key.split(S3_FILE_SEPERATOR);
        CreateBelgeInput createBelgeInput = new CreateBelgeInput();
        createBelgeInput.setOrgOid(keyArray[0]);
        createBelgeInput.setBelgeTuru(keyArray[1]);
        createBelgeInput.setBelgeNo(keyArray[2]);
        createBelgeInput.setTaslak(keyNullControl(keyArray[3]));
        createBelgeInput.setMemur(keyNullControl(keyArray[4]));
        createBelgeInput.setSef(keyNullControl(keyArray[5]));
        createBelgeInput.setMuduryrd(keyNullControl(keyArray[6]));
        createBelgeInput.setMudur(keyNullControl(keyArray[7]));

        return createBelgeInput;
    }

    private String keyNullControl(String deger) {
        return deger.equals("NULL") ? null : deger;
    }

    public String createKey(CreateBelgeInput createBelgeInput) {
        return createBelgeInput.getOrgOid() + S3_FILE_SEPERATOR + createBelgeInput.getBelgeTuru() + S3_FILE_SEPERATOR + createBelgeInput.getBelgeNo();
    }

    public String createBucketName(String belgeNo) {
        return S3_BUCKET_NAME_PREFIX + belgeNo.substring(0,4);
    }

    public String createBucketName() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

        return S3_BUCKET_NAME_PREFIX + sdf.format(date);
    }

}
