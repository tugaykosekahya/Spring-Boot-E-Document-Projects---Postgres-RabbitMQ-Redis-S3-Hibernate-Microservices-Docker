package tr.gov.gib.evdbelge.evdbelgehazirlama.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgehazirlama.dao.ParametrelerDao;
import tr.gov.gib.evdbelge.evdbelgehazirlama.entity.Parametreler;
import tr.gov.gib.evdbelge.evdbelgehazirlama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.ParametrelerService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class ParametrelerServiceImpl extends AbstractJPABaseServiceImpl<Parametreler> implements ParametrelerService {
    private static final String PARAMETRELER_MAP = "parametreler";
    private final ParametrelerDao parametrelerDao;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name="readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    @Autowired
    public ParametrelerServiceImpl(ParametrelerDao parametrelerDao) {
        this.parametrelerDao = parametrelerDao;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return parametrelerDao;
    }

    @Override
    protected void preInsertValid(Parametreler parametreler) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(Parametreler parametreler) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(Parametreler parametreler) throws GibBusinessException {

    }

    @Override
    protected void validate(Parametreler parametreler) throws GibBusinessException {

    }

    public List getAllParametrelerFromDB() throws GibRemoteException {
        List<Parametreler> parametreList;
        try {
            parametreList = parametrelerDao.findAll();
            return parametreList;
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_HATASI.message(), e);
            throw new GibRemoteException(Messages.SISTEM_HATASI.message(), ""); //todo ex atilirsa borc sorgulanmayacak.
        }
    }

    public List getCanliParametreFromRedis(String... key) throws GibException {
        try {
            return readOnlyHashOperations.multiGet(PARAMETRELER_MAP, Arrays.asList(key));
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public String getCanliParametreFromRedis(String key) throws GibException {
        try {
            return readOnlyHashOperations.get(PARAMETRELER_MAP, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public void putParametreToRedis(String key, String value) {
        try {
            hashOperations.put(PARAMETRELER_MAP, key, value);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
        }
    }

}
