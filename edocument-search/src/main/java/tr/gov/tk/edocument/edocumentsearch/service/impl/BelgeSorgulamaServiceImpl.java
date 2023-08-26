package tr.gov.gib.evdbelge.evdbelgesorgulama.service.impl;

import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgesorgulama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeAkisGuncelle;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeSorgula;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateGenericInsert;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeAkisGuncelleResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeGenericInsertResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeSorguResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.service.BelgeAkisService;
import tr.gov.gib.evdbelge.evdbelgesorgulama.service.BelgeService;
import tr.gov.gib.evdbelge.evdbelgesorgulama.service.BelgeSorgulamaService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Component
public class BelgeSorgulamaServiceImpl implements BelgeSorgulamaService {
    private final BelgeService belgeService;
    private final BelgeAkisService belgeAkisService;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeSorgulamaServiceImpl(BelgeService belgeService, BelgeAkisService belgeAkisService) {
        this.belgeService = belgeService;
        this.belgeAkisService = belgeAkisService;
    }

    @Override
    public BelgeSorguResponse belgeSorgula(CreateBelgeSorgula createBelgeSorgula) throws GibException {
        BelgeSorguResponse belgeSorguResponse = new BelgeSorguResponse();
        belgeSorguResponse.setQueryResult(belgeService.belgeSorgula(createBelgeSorgula.getQuery()));
        return belgeSorguResponse;
    }

    @Override
    public BelgeGenericInsertResponse nativeInsert(CreateGenericInsert createGenericInsert) throws GibException {
        try {
            long id = belgeService.nativeInsert(createGenericInsert.getQuery(), createGenericInsert.isIdDonulecek());
            return createGenericInsertResponse(1, "Başarılı", id);
        } catch (GibException e) {
            return createGenericInsertResponse(0, e.getMessage(), 0L);
        } catch (Exception e) {
            LOGGER.error(Messages.BEKLENMEYEN_HATA.message() , e);
            return createGenericInsertResponse(0, Messages.VERI_TABANI_HATASI.message(), 0L);
        }
    }

    @Override
    public BelgeAkisGuncelleResponse akisGuncelle(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibException {
        if(createBelgeAkisGuncelle.getDurum() <= 0)
            return createAkisGuncelleResponse(0, "durum:" + createBelgeAkisGuncelle.getDurum() + " akış güncellenemedi.");
        try {
            belgeAkisService.akisGuncelle(createBelgeAkisGuncelle);
            return createAkisGuncelleResponse(1, "Başarılı");
        } catch (GibException e) {
            return createAkisGuncelleResponse(0, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(Messages.BEKLENMEYEN_HATA.message() , e);
            return createAkisGuncelleResponse(0, Messages.VERI_TABANI_HATASI.message());
        }
    }

    private static BelgeAkisGuncelleResponse createAkisGuncelleResponse(int durum, String msg) {
        BelgeAkisGuncelleResponse belgeAkisGuncelleResponse = new BelgeAkisGuncelleResponse();
        belgeAkisGuncelleResponse.setResultMsg(msg);
        belgeAkisGuncelleResponse.setResultCode((short) durum);
        return belgeAkisGuncelleResponse;
    }

    private static BelgeGenericInsertResponse createGenericInsertResponse(int durum, String msg, long id) {
        BelgeGenericInsertResponse belgeGenericInsertResponse = new BelgeGenericInsertResponse();
        belgeGenericInsertResponse.setResultMsg(msg);
        belgeGenericInsertResponse.setResultCode((short) durum);
        belgeGenericInsertResponse.setId(id);
        return belgeGenericInsertResponse;
    }
}
