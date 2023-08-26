package tr.gov.gib.evdbelge.evdbelgeaktarma.service.impl;

import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgeaktarma.dao.BelgeDao;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.BelgeAkis;
import tr.gov.gib.evdbelge.evdbelgeaktarma.message.Messages;
import tr.gov.gib.evdbelge.evdbelgeaktarma.service.BelgeService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class BelgeServiceImpl extends AbstractJPABaseServiceImpl<Belge> implements BelgeService {
    private final BelgeDao belgeDao;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeServiceImpl(BelgeDao belgeDao) {
        this.belgeDao = belgeDao;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return belgeDao;
    }

    @Override
    protected void preInsertValid(Belge entity) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(Belge entity) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(Belge entity) throws GibBusinessException {

    }

    @Override
    protected void validate(Belge entity) throws GibBusinessException {

    }

    @Override
    public void belgeKaydet(Belge belge) throws GibException {
        try {
            BelgeAkis belgeAkis = new BelgeAkis();
            belgeAkis.setKullaniciKodu(belge.getKullanicikodu());
            belgeAkis.setNextnodeuserid(belge.getNextnodeuserid());
            belgeAkis.setBelge(belge);
            belge.setBelgeAkislar(new ArrayList<>(Collections.singletonList(belgeAkis)));
            belgeDao.save(belge);
        } catch (Exception e) {
            String data;
            data = belge.toString();
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message(belge.getBelgeno() + ", " + e.getMessage() + "\n" + data) , e);
            throw new GibRemoteException(Messages.BEKLENMEYEN_HATA.message(e.getMessage()), "");
        }
    }

    @Override
    public boolean belgeVarMi(String belgeno, String orgoid, short belgeturu) throws GibException {
        try {
            return belgeDao.existsByOrgoidAndBelgenoAndAndBelgeturu(orgoid, belgeno, belgeturu);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message(belgeno + ", " + orgoid + ", " + belgeturu + ", " + e.getMessage() + "\n") , e);
            throw new GibRemoteException(Messages.BEKLENMEYEN_HATA.message(e.getMessage()), "");
        }
    }

    @Override
    public Belge belgeGetir(String belgeno, String orgoid, short belgeturu) throws GibException {
        try {
            return belgeDao.findByOrgoidAndBelgenoAndAndBelgeturu(orgoid, belgeno, belgeturu);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message(belgeno + ", " + orgoid + ", " + belgeturu + ", " + e.getMessage() + "\n") , e);
            throw new GibRemoteException(Messages.HATA.message(e.getMessage()), "");
        }
    }

}
