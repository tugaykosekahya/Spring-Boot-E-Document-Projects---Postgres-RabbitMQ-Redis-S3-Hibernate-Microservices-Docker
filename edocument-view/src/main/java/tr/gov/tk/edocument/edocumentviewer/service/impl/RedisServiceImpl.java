package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.impl;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.message.Messages;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.RedisService;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import javax.annotation.Resource;

@Component
public class RedisServiceImpl implements RedisService {
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name = "readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    private final GibLogger logger = GibLoggerFactory.getLogger();
    private static final String SESSION_MAP = "belgeGoruntulemeSession";


    public boolean hasSessionInRedis(String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.hasKey(SESSION_MAP, key);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public void deleteSessionFromRedis(String key) throws GibRemoteException {
        try {
            hashOperations.delete(SESSION_MAP, key);
        } catch (Exception e) {
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public void putSessionToRedis(String key, String value) throws GibRemoteException {
        try {
            hashOperations.put(SESSION_MAP, key, value);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

    public String getSessionFromRedis(String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.get(SESSION_MAP, key);
        } catch (Exception e) {
            logger.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(), "");
        }
    }

}
