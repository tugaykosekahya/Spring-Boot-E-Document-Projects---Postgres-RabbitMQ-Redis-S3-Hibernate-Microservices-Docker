package tr.gov.gib.evdbelge.evdbelgeteblig.service;

import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedefSonuc;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeHedefSonucService extends AbstractBaseService<BelgeHedefSonuc> {
    void belgeHedefSonucKaydet(BelgeHedefSonuc belgeHedefSonuc) throws GibException;
}
