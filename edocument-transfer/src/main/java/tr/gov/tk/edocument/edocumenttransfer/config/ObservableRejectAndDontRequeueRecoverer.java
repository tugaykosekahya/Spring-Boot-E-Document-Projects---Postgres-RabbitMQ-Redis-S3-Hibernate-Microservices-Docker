package tr.gov.gib.evdbelge.evdbelgeaktarma.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import tr.gov.gib.evdbelge.evdbelgeaktarma.message.Messages;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

public class ObservableRejectAndDontRequeueRecoverer extends RejectAndDontRequeueRecoverer {
    private static final Gson GSON = new Gson();
    private static final String BELGE_MAP = "belgeIsleme";
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    @Value("${spring.rabbitmq.evdbelge.queue}")
    private String queueName;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;

    @Override
    public void recover(Message message, Throwable cause) {
        if(queueName.equals("evdbelgeAktarimDlq")) {
            JsonObject json = GSON.fromJson(GSON.fromJson(new String(message.getBody(), StandardCharsets.UTF_8), JsonPrimitive.class).getAsString(), JsonObject.class);
            String belgeIslemeKey = json.getAsJsonObject("jsonData").get("orgoid").getAsString() + ";" + json.getAsJsonObject("jsonData").get("belgeturu").getAsString() + ";" + json.getAsJsonObject("jsonData").get("belgeno").getAsString();
            try {
                deleteBelgeFromRedis(belgeIslemeKey);
            } catch (GibRemoteException e) {
            }
        }
        super.recover(message, cause);
    }

    public void deleteBelgeFromRedis(String key) throws GibRemoteException {
        try {
            hashOperations.delete(BELGE_MAP, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }
}
