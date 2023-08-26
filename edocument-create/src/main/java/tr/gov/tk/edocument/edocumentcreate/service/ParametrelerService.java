package tr.gov.gib.evdbelge.evdbelgehazirlama.service;

import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import java.util.List;

public interface ParametrelerService {
    List getCanliParametreFromRedis(String... key) throws GibException;
    List getAllParametrelerFromDB() throws GibRemoteException;
    void putParametreToRedis(String key, String value);
}
