package tr.gov.gib.evdbelge.evdbelgeteblig.service.impl;

import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgeteblig.dao.BelgeHedefSonucDao;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedefSonuc;
import tr.gov.gib.evdbelge.evdbelgeteblig.message.Messages;
import tr.gov.gib.evdbelge.evdbelgeteblig.service.BelgeHedefSonucService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Service
public class BelgeHedefSonucServiceImpl extends AbstractJPABaseServiceImpl<BelgeHedefSonuc> implements BelgeHedefSonucService {
    private final BelgeHedefSonucDao belgeHedefSonucDao;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeHedefSonucServiceImpl(BelgeHedefSonucDao belgeHedefSonucDao) {
        this.belgeHedefSonucDao = belgeHedefSonucDao;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return belgeHedefSonucDao;
    }

    @Override
    protected void preInsertValid(BelgeHedefSonuc entity) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(BelgeHedefSonuc entity) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(BelgeHedefSonuc entity) throws GibBusinessException {

    }

    @Override
    protected void validate(BelgeHedefSonuc entity) throws GibBusinessException {

    }

    @Override
    public void belgeHedefSonucKaydet(BelgeHedefSonuc belgeHedefSonuc) throws GibException {
        try {
            save(belgeHedefSonuc);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge_hedef_id : " + belgeHedefSonuc.getBelgeHedef().getId()+ " , " + e.getMessage() + "\n") , e);
            throw new GibException(Messages.BEKLENMEYEN_HATA.message(), "");
        }
    }
}
