package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service;

import tr.gov.gib.evdbelge.evdbelgegoruntuleme.entity.Belge;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeService extends AbstractBaseService<Belge> {
    void belgeKaydet(Belge belge) throws GibException;
}
