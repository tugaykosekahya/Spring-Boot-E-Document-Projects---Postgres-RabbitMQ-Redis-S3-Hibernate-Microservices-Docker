package tr.gov.gib.evdbelge.evdbelgepdfolusturma.service;

import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import java.io.InputStream;
import java.util.HashMap;

public interface S3Service {
    void uploadToObjectStorage(HashMap map, InputStream fileInputStream) throws GibRemoteException;
    InputStream downloadFileFromObjectStorage(String bucketName, String path);
    void deleteFileFromObjectStorage(HashMap map);
}
