package tr.gov.gib.evdbelge.evdbelgesorgulama.service.impl;

import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgesorgulama.dao.BelgeDao;
import tr.gov.gib.evdbelge.evdbelgesorgulama.dao.BelgeDaoJpa;
import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgesorgulama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgesorgulama.service.BelgeService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import java.util.List;

@Service
public class BelgeServiceImpl extends AbstractJPABaseServiceImpl<Belge> implements BelgeService {
    private final BelgeDao belgeDao;
    private final BelgeDaoJpa belgeDaoJpa;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeServiceImpl(BelgeDao belgeDao, BelgeDaoJpa belgeDaoJpa) {
        this.belgeDao = belgeDao;
        this.belgeDaoJpa = belgeDaoJpa;
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
    public List belgeSorgula(String query) throws GibException {
        try {
            return belgeDaoJpa.nativeSorgu(query);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_HATASI.message() , e);
            throw new GibRemoteException(Messages.VERI_TABANI_HATASI_DETAYLI.message(e.getMessage()), "");
        }
    }

    @Override
    public long nativeInsert(String query, boolean idDonulecek) throws GibException {
        try {
            return belgeDaoJpa.nativeInsert(query, idDonulecek);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_HATASI.message() , e);
            throw new GibRemoteException(Messages.VERI_TABANI_HATASI_DETAYLI.message(e.getMessage()), "");
        }
    }
}
