package tr.gov.gib.evdbelge.evdbelgesorgulama.service;

import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeAkisGuncelle;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeSorgula;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateGenericInsert;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeAkisGuncelleResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeGenericInsertResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeSorguResponse;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeSorgulamaService {
    BelgeSorguResponse belgeSorgula(CreateBelgeSorgula createBelgeSorgula) throws GibException;
    BelgeGenericInsertResponse nativeInsert(CreateGenericInsert createGenericInsert) throws GibException;
    BelgeAkisGuncelleResponse akisGuncelle(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibException;
}
