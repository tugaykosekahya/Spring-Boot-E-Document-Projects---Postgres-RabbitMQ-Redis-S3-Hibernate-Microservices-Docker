package tr.gov.gib.evdbelge.evdbelgesorgulama.service;

import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.BelgeAkis;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeAkisGuncelle;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeAkisService extends AbstractBaseService<BelgeAkis> {
    void akisGuncelle(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibException;
}
