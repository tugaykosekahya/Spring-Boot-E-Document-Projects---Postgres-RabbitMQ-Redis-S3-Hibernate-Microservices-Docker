package tr.gov.gib.evdbelge.evdbelgepdfimzalama.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.message.Messages;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

public class ObservableRejectAndDontRequeueRecoverer extends RejectAndDontRequeueRecoverer {
    private static final Gson GSON = new Gson();
    private static final String BELGE_MAP = "belgeIsleme";
    @Value("${spring.rabbitmq.evdbelge.pdfImza.queue}")
    private String queueName;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;

    @Override
    public void recover(Message message, Throwable cause) {
        if(queueName.equals("evdbelgePdfImzaDlq")) {
            JsonObject json = GSON.fromJson(GSON.fromJson(new String(message.getBody(), StandardCharsets.UTF_8), JsonPrimitive.class).getAsString(), JsonObject.class);
            String belgeno = json.getAsJsonObject("jsonData").get("belgeno").getAsString();
            try {
                deleteBelgeFromRedis(belgeno);
            } catch (GibRemoteException e) {
            }
        }
        super.recover(message, cause);
    }

    public void deleteBelgeFromRedis(String key) throws GibRemoteException {
        try {
            hashOperations.delete(BELGE_MAP, key);
        } catch (Exception e) {
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }
}
