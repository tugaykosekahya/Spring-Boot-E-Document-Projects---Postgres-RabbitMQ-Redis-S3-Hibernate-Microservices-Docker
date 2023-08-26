package tr.gov.gib.evdbelge.evdbelgeteblig.dao;

import org.springframework.stereotype.Component;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

@Component
public interface BelgeDaoJpa {
    void belgeUpdate(long belgeId, short durum) throws GibException;
}
