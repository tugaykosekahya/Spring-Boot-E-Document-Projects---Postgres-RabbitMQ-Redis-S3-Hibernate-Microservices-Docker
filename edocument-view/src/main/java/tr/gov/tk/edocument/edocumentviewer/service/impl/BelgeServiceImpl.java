package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.impl;

import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.dao.BelgeDao;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.message.Messages;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.BelgeService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Service
public class BelgeServiceImpl extends AbstractJPABaseServiceImpl<Belge> implements BelgeService {
    private final BelgeDao belgeDao;
    private final GibObjectMapper gibObjectMapper;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeServiceImpl(BelgeDao belgeDao, GibObjectMapper gibObjectMapper) {
        this.belgeDao = belgeDao;
        this.gibObjectMapper = gibObjectMapper;
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
            belgeDao.save(belge);
        } catch (Exception e) {
            String data;
            data = gibObjectMapper.toJsonString(belge);
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message(belge.getBelgeno() + ", " + e.getMessage() + "\n" + data) , e);
            throw new GibException(Messages.BEKLENMEYEN_HATA.message(), "");
        }
    }
}
