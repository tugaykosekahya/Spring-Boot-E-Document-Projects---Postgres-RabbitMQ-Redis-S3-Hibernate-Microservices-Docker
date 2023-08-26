package tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.impl;

import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.dao.BelgeAkisDao;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.entity.BelgeAkis;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.message.Messages;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.BelgeAkisService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Component
public class BelgeAkisServiceImpl extends AbstractJPABaseServiceImpl<BelgeAkis> implements BelgeAkisService {
    private final BelgeAkisDao belgeAkisDao;
    private final BelgeServiceImpl belgeService;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeAkisServiceImpl(BelgeAkisDao belgeAkisDao, BelgeServiceImpl belgeService) {
        this.belgeAkisDao = belgeAkisDao;
        this.belgeService = belgeService;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return belgeAkisDao;
    }

    @Override
    protected void preInsertValid(BelgeAkis entity) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(BelgeAkis entity) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(BelgeAkis entity) throws GibBusinessException {

    }

    @Override
    protected void validate(BelgeAkis entity) throws GibBusinessException {

    }

    public void akisGuncelle(long belgeId) throws GibException {
        BelgeAkis belgeAkis = new BelgeAkis();
        belgeAkis.setKullaniciKodu("GELBIM");
        belgeAkis.setNextnodeuserid("PDF Üret");
        Belge b = (Belge) belgeService.getDao().getReferenceById(belgeId);
        belgeAkis.setBelge(b);
        akisKaydet(belgeAkis);
    }

    public void akisKaydet(BelgeAkis belgeAkis) throws GibException {
        try {
            save(belgeAkis);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge_akis kaydedilirken hata oluştu") , e);
            throw new GibException(Messages.VERI_TABANI_KAYIT_HATASI.message("belge_akis kaydedilirken hata oluştu"), "");
        }
    }

}
