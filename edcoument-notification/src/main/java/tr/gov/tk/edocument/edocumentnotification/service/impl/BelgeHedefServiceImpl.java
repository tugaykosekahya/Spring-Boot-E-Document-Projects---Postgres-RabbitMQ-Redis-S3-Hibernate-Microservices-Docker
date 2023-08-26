package tr.gov.gib.evdbelge.evdbelgeteblig.service.impl;

import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgeteblig.dao.BelgeHedefDao;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedef;
import tr.gov.gib.evdbelge.evdbelgeteblig.message.Messages;
import tr.gov.gib.evdbelge.evdbelgeteblig.service.BelgeHedefService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Service
public class BelgeHedefServiceImpl extends AbstractJPABaseServiceImpl<BelgeHedef> implements BelgeHedefService {
    private final BelgeHedefDao belgeHedefDao;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeHedefServiceImpl(BelgeHedefDao belgeHedefDao) {
        this.belgeHedefDao = belgeHedefDao;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return belgeHedefDao;
    }

    @Override
    protected void preInsertValid(BelgeHedef entity) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(BelgeHedef entity) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(BelgeHedef entity) throws GibBusinessException {

    }

    @Override
    protected void validate(BelgeHedef entity) throws GibBusinessException {

    }

    @Override
    public boolean belgeHedefVarMi(Belge belge) throws GibException {
        try {
            return belgeHedefDao.existsBelgeHedefByBelgeHedef(belge);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge_id : " + belge.getId()+ " , " + e.getMessage() + "\n") , e);
            throw new GibException(Messages.BEKLENMEYEN_HATA.message(), "");
        }
    }
}
