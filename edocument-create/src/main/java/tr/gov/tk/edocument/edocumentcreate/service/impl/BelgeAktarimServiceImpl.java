package tr.gov.gib.evdbelge.evdbelgehazirlama.service.impl;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tr.gov.gib.evdbelge.evdbelgehazirlama.dao.QueueDao;
import tr.gov.gib.evdbelge.evdbelgehazirlama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BelgeAktarimService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thsexternal.RestTemplateService;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thsutils.DateUtils;
import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;

@Component
public class BelgeAktarimServiceImpl implements BelgeAktarimService {
    private static final String BELGE_KUYRUKTA = "0";
    private static final String BELGE_AKTARILDI = "1";
    private static final String BELGE_MAP = "belgeIsleme";

    @Value("${spring.rabbitmq.api.url}")
    private String rabbitUrl;

    @Value("${spring.rabbitmq.api.token}")
    private String rabbitToken;

    private final RestTemplateService restTemplateUtilityService;
    private final QueueDao queueDao;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name="readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    public BelgeAktarimServiceImpl(QueueDao queueDao, RestTemplateService restTemplateUtilityService) {
        this.queueDao = queueDao;
        this.restTemplateUtilityService = restTemplateUtilityService;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void kuyrukVeRediseAt(JsonObject json) throws GibException {
        String key = json.get("orgoid").getAsString() + ";" + json.get("belgeturu").getAsString() + ";" + json.get("belgeno").getAsString();
        if(hasBelgeInRedis(key))
            return;
        pushAktarimQueue(json);
        JsonObject jsonRedis = new JsonObject();
        jsonRedis.addProperty("status", BELGE_KUYRUKTA);
        jsonRedis.addProperty("optime", DateUtils.getDateTime());
        putBelgeToRedis(key, jsonRedis.toString());
    }

    public boolean hasBelgeInRedis(String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.hasKey(BELGE_MAP, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }

    public void putBelgeToRedis(String key, String json) throws GibRemoteException {
        try {
            hashOperations.put(BELGE_MAP, key, json);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }

    public void pushAktarimQueue(JsonObject json) throws GibRemoteException {
        try {
            queueDao.pushAktarimQueue(json);
        } catch (Exception e) {
            LOGGER.error(Messages.KUYRUK_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.KUYRUK_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }

    public String queueRequest() throws GibException {
        ResponseEntity<Object> response;
        try{
            response = this.restTemplateUtilityService
                    .exchangeAll(null, Object.class, URI.create(rabbitUrl), HttpMethod.GET, rabbitToken);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GibRemoteException(Messages.SISTEM_HATASI.message(), "");
        }
        var sonuc = ((HashMap) response.getBody());
        if(sonuc == null) {
            LOGGER.error(Messages.SERVIS_HATASI.message("queues evdbelgeAktarim"));
            throw new GibRemoteException(Messages.SISTEM_HATASI.message(), "");
        }
        var data = sonuc.get("messages").toString();
        if(data == null){
            LOGGER.error(Messages.SERVIS_HATASI.message("queues evdbelgeAktarim"));
            throw new GibRemoteException(Messages.SISTEM_HATASI.message(), "");
        }
        return data;
    }
}
