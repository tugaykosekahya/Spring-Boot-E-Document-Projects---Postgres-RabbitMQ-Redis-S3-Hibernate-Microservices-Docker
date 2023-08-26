package tr.gov.gib.evdbelge.evdbelgesorgulama.dao;

import org.springframework.stereotype.Component;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import java.util.List;

@Component
public interface BelgeDaoJpa {
    List nativeSorgu(String queryString);
    long nativeInsert(String queryString, boolean idDonulecek) throws GibRemoteException, GibBusinessException;
    void belgeUpdate(long belgeId, int durum, String nextnodeuserid) throws GibException;
}
