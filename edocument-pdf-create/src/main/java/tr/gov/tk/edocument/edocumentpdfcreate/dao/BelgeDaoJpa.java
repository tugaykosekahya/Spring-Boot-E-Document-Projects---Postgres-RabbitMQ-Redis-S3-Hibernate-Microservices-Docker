package tr.gov.gib.evdbelge.evdbelgepdfolusturma.dao;

import org.springframework.stereotype.Component;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

import java.util.List;

@Component
public interface BelgeDaoJpa {
    List nativeSorgu(String type);
    void belgeUpdate(long belgeId, int durum) throws GibException;
}
