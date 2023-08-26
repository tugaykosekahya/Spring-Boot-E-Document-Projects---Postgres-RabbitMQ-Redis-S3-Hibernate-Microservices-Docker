package tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tr.com.cs.s3point.S3AccessObject;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.S3Service;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import java.io.InputStream;
import java.util.HashMap;

@Component
public class S3ServiceImpl implements S3Service {
    private S3AccessObject s3AccessObject;

    @Value("${objectstorage.s3FileSeperator}") public String S3_FILE_SEPERATOR;
    @Value("${objectstorage.s3FileExtention}") public String S3_FILE_EXTENSION;
    @Value("${objectstorage.s3Endpoint}") public String S3_ENDPOINT;
    @Value("${objectstorage.s3Namespace}") public String S3_NAMESPACE;
    @Value("${objectstorage.s3AccessKey}") public String S3_ACCESS_KEY;
    @Value("${objectstorage.s3SecretKey}") public String S3_SECRET_KEY;
    @Value("${objectstorage.s3BucketNamePrefix}") public String S3_BUCKET_NAME_PREFIX;

    private void openConnection(String s3Bucket) {
        s3AccessObject = new S3AccessObject(S3_ENDPOINT, S3_NAMESPACE, S3_ACCESS_KEY, S3_SECRET_KEY, s3Bucket);
    }

    private S3AccessObject getS3AccessObject(String bucketName) {
        if (s3AccessObject == null) {
            openConnection(bucketName);
        }
        return s3AccessObject;
    }

    public InputStream downloadFileFromObjectStorage(String bucketName, String path) {
        S3AccessObject s3 = getS3AccessObject(bucketName);
        S3Object obj = s3.getObject(bucketName, path);
        return obj.getObjectContent();
    }

    /*public String makeS3FilePath(String yuklemeZamaniAy, String kullaniciKodu, String fileName) {
        return yuklemeZamaniAy + S3_FILE_SEPERATOR + kullaniciKodu.substring(0,3) + S3_FILE_SEPERATOR + kullaniciKodu + S3_FILE_SEPERATOR + fileName;
    }*/

    private void writeFile(String bucketName, ObjectMetadata metadata, InputStream fileStream, String dizin) throws GibRemoteException {
        S3AccessObject s3 = getS3AccessObject(bucketName);
        PutObjectResult putResult = s3.putObjectwithMetaData(dizin, fileStream, metadata, false);
        if (putResult == null)
            throw new GibRemoteException("Failed to write " + dizin + " keyed file to object storage.", "");
    }

    private void deleteFile(String bucketName, String path) {
        S3AccessObject s3 = getS3AccessObject(bucketName);
        s3.deleteObject(bucketName, path);
    }

    public void deleteFileFromObjectStorage(HashMap map) {
        deleteFile(S3_BUCKET_NAME_PREFIX + map.get("optime").toString().substring(0, 4), map.get("fileName").toString());
    }

    public ObjectMetadata createMetaData(HashMap map) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserMetadata(map);
        return objectMetadata;
    }

    /*private long getFileSize(String bucketName, String path) {
        S3AccessObject s3 = getS3AccessObject(bucketName);
        return s3.getObject(bucketName, path).getObjectMetadata().getContentLength();
    }

    public JSON uploadFileToObjectStorage(JSON inJSON) throws Exception {
        makeZipFromFile(inJSON);
        writeFile(S3_BUCKET_NAME_PREFIX + inJSON.getString("yuklemeZamani").substring(0, 4), createMetaData((HashMap) inJSON.get("metaDataMap")), ((InputStream) inJSON.get("zipInputStream")),
                makeS3FilePath(inJSON.getString("yuklemeZamani").substring(4, 6), inJSON.getString("kullaniciKodu"), inJSON.getString("zipFileName")) );
        return inJSON;
    }

    public void uploadZipToObjectStorageForUploadedFiles(JSON inJSON) throws Exception {
        writeFile(S3_BUCKET_NAME_PREFIX + inJSON.getString("yuklemeZamani").substring(0, 4), createMetaData((HashMap) inJSON.get("metaDataMap")), ((InputStream) inJSON.get("zipInputStream")),
                makeS3FilePath(inJSON.getString("yuklemeZamani").substring(4, 6), inJSON.getString("kullaniciKodu"), inJSON.getString("zipFileName")) );
        return inJSON;
    }

    public long getFileSizeFromObjectStorage(String yuklemeZamani, String kullaniciKodu, String zipFileName) {
        String path = makeS3FilePath(yuklemeZamani.substring(4, 6), kullaniciKodu, zipFileName);
        return getFileSize(S3_BUCKET_NAME_PREFIX + yuklemeZamani.substring(0, 4), path);
    }*/

    public void uploadToObjectStorage(HashMap map, InputStream fileInputStream) throws GibRemoteException {
        writeFile(map.get("bucketName").toString(), createMetaData(map), fileInputStream, map.get("fileName").toString());
    }

    /*private JSON makeZipFromFile(JSON inJSON) throws Exception {
        JSON fileObj = (JSON) inJSON.get("file");
        String fileTypeExtension = inJSON.existsInJSON("fileType") ? inJSON.getString("fileType") : ".json";
        String fileOid = fileObj.getString("fileOid");
        String zipFileName = fileOid;
        InputStream fis = (InputStream) fileObj.get("fileInputStream");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry zipEntry = new ZipEntry(fileOid + fileTypeExtension);
        zos.putNextEntry(zipEntry);
        IOUtils.copy(fis, zos);
        zos.closeEntry();
        zos.close();
        inJSON.put("zipInputStream", new ByteArrayInputStream(baos.toByteArray()));
        inJSON.put("zipFileName", zipFileName);
        fis.close();
        baos.close();
        return inJSON;
    }*/

    /*public JSON createFileJSON(String fileOid, InputStream fileInputStream) {
        JSON fileJSON = new JSON();
        fileJSON.put("fileOid", fileOid);
        fileJSON.put("fileInputStream", fileInputStream);
        return fileJSON;
    }

    public static String readFromStreamtoString(InputStream is) throws Exception {
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        String fileContent = "";
        try
        {
            while( (bytesRead = is.read(buffer)) != -1){
                fileContent += new String(buffer, 0, bytesRead);
            }
            is.close();
        }
        finally
        {
            if(is != null)
                is.close();
        }
        return fileContent;
    }*/
}

