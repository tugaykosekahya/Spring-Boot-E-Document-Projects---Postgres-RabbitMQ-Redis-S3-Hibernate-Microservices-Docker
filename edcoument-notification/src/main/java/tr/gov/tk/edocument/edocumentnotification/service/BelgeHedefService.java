package tr.gov.gib.evdbelge.evdbelgeteblig.service;

import tr.gov.gib.evdbelge.evdbelgeteblig.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedef;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeHedefService extends AbstractBaseService<BelgeHedef> {
    boolean belgeHedefVarMi(Belge belge) throws GibException;
}
