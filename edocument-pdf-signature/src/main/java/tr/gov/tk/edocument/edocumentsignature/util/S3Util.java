package tr.gov.gib.evdbelge.evdbelgepdfimzalama.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

@Component
public class S3Util implements AutoCloseable {
    @Value("${objectstorage.s3FileSeperator}")
    private String S3_FILE_SEPERATOR;
    @Value("${objectstorage.s3FileExtention}")
    private String S3_FILE_EXTENSION;
    @Value("${objectstorage.s3Endpoint}")
    private String S3_ENDPOINT;
    @Value("${objectstorage.s3Namespace}")
    private String S3_NAMESPACE;
    @Value("${objectstorage.s3AccessKey}")
    public String S3_ACCESS_KEY;
    @Value("${objectstorage.s3SecretKey}")
    private String S3_SECRET_KEY;
    @Value("${objectstorage.s3BucketNamePrefix}")
    private static AmazonS3 S3CLIENT = null;
    private static TransferManager TRANSFER_MANAGER = null;


    private AmazonS3 getS3AccessObject() {
        return createS3AccessObject();

    }

    synchronized AmazonS3 createS3AccessObject() {
        if (S3CLIENT == null) {
            try {
                BasicAWSCredentials awsCreds = new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY);
                ClientConfiguration conf = new ClientConfiguration();
                conf.setMaxConnections(300);
                conf.setSocketTimeout(600000);
                S3CLIENT = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                        .withPathStyleAccessEnabled(true).withClientConfiguration(conf)
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(S3_ENDPOINT, Regions.DEFAULT_REGION.getName())).build();
            } catch (Throwable t) {
                t.printStackTrace();
                throw t;
            }
        }
        return S3CLIENT;
    }

    private TransferManager getTransferManager(AmazonS3 s3Client) {
        return createTransferManager(s3Client);

    }

    private synchronized TransferManager createTransferManager(AmazonS3 s3Client) {
        if (TRANSFER_MANAGER == null) {
            try {
                TRANSFER_MANAGER = TransferManagerBuilder.standard().withS3Client(s3Client).build();
            } catch (Throwable t) {
                t.printStackTrace();
                throw t;
            }
        }
        return TRANSFER_MANAGER;
    }

    public void dosyaYukleWithFile(String bucketName, String filePath, File item, InputStream is) {
        try {
            long start = System.currentTimeMillis();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(item.getTotalSpace());

            Upload upload = getTransferManager(getS3AccessObject()).upload(bucketName, filePath, item);
            upload.waitForCompletion();

            long sure = System.currentTimeMillis() - start;
            System.out.println("S3 Upload: " + item.getTotalSpace() + "/" + sure);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public InputStream dosyaInputStream(String bucketName, String path) throws GibRemoteException {
        InputStream inputStream = null;
        try {
            inputStream = getS3AccessObject().getObject(bucketName, path).getObjectContent();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (inputStream == null) {
            throw new GibRemoteException("Failed to read " + path + " keyed file to object storage.", "");
        } else {
            return inputStream;
        }
    }

    public boolean dosyaSil(String bucketName, String path) {
        try {
            getS3AccessObject().deleteObject(bucketName, path);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public void dosyaYukleS3(HashMap map, byte[] obj) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(map);
        metadata.setContentLength(obj.length);
        PutObjectRequest request = new PutObjectRequest(map.get("bucketName").toString(), map.get("fileName").toString(), new ByteArrayInputStream(obj), metadata);
        PutObjectResult putResult = getS3AccessObject().putObject(request);
        if (putResult == null)
            throw new GibRemoteException("Failed to write " + map.get("fileName").toString() + " keyed file to object storage.", "");
    }

//    public static List getFileKeyList(String bucketName,String bucketYil, String prefix, String delimiter) {
//        ObjectListing objectListing = getS3AccessObject().listObjects((new ListObjectsRequest()).withBucketName(getBucketName(bucketName,bucketYil)).withPrefix(prefix).withDelimiter(delimiter));
//        List<String> keyList = new ArrayList<>();
//        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//            String summaryKey = objectSummary.getKey();
//            keyList.add(summaryKey);
//        }
//        return keyList;
//    }
//
//    public static long getFileSize(String bucketName, String bucketYil, String fileName) {
//        return getS3AccessObject().getObject(S3_BUCKET_NAME_PREFIX, fileName).getObjectMetadata().getContentLength();
//    }
//
//    public static String getFilePath(String mainPath, String tarih, String vkn) {
//        return mainPath + S3_FILE_SEPERATOR + tarih.substring(0, 4) + S3_FILE_SEPERATOR + tarih.substring(4, 6) + S3_FILE_SEPERATOR + tarih.substring(6, 8) + S3_FILE_SEPERATOR + vkn + S3_FILE_SEPERATOR;
//    }
//
//    public static String convertDateTimeToS3Path(String createTime) {
//        return createTime.substring(0, 4) + S3_FILE_SEPERATOR+ createTime.substring(4, 6) + S3_FILE_SEPERATOR + createTime.substring(6, 8) + S3_FILE_SEPERATOR + createTime.substring(8, 10) + S3_FILE_SEPERATOR;
//    }

//    protected void finalize() throws Throwable {
//        super.finalize();
//
//        try {
//            if (TRANSFER_MANAGER != null) {
//                TRANSFER_MANAGER.shutdownNow();
//            }
//        } catch (Exception e) {
//        }
//
//        try {
//            if (S3CLIENT != null) {
//                S3CLIENT.shutdown();
//            }
//        } catch (Exception e) {
//        }
//    }

    @Override
    public void close() {
        try {
            if (TRANSFER_MANAGER != null) {
                TRANSFER_MANAGER.shutdownNow();
            }
            if (S3CLIENT != null) {
                S3CLIENT.shutdown();
            }
        } catch (Exception e) {
        }

    }
}
