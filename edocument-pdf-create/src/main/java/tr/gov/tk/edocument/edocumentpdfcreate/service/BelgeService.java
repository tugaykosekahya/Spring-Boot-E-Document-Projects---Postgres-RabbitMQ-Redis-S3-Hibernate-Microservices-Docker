package tr.gov.gib.evdbelge.evdbelgepdfolusturma.service;

import tr.gov.gib.evdbelge.evdbelgepdfolusturma.entity.Belge;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeService extends AbstractBaseService<Belge> {
    void belgeKaydet(Belge belge) throws GibException;
    boolean belgeVarMi(String belgeno, String orgoid, int belgeturu) throws GibException;
}
