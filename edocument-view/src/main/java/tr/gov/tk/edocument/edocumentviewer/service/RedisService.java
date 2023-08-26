package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service;

import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

public interface RedisService {

    boolean hasSessionInRedis(String key) throws GibRemoteException;

    void deleteSessionFromRedis(String key) throws GibRemoteException;

    void putSessionToRedis(String key, String value) throws GibRemoteException;

    String getSessionFromRedis(String key) throws GibRemoteException;
}
