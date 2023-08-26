package tr.gov.gib.evdbelge.evdbelgepdfimzalama.service;

import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.BelgeAkis;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeAkisService extends AbstractBaseService<BelgeAkis> {
    void akisGuncelle(long belgeId) throws GibException;
}
