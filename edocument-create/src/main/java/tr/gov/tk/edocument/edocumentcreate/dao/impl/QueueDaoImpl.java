package tr.gov.gib.evdbelge.evdbelgehazirlama.dao.impl;

import com.google.gson.JsonObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgehazirlama.dao.QueueDao;
import tr.gov.gib.tahsilat.thsutils.DateUtils;

@Component
public class QueueDaoImpl implements QueueDao {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.evdbelge.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.evdbelge.routingKey}")
    private String ebaRoutingkey;

    @Override
    public void pushAktarimQueue(JsonObject jsondata) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("jsonData", jsondata);
        jsonObj.addProperty("optime", DateUtils.getDateTime());
        rabbitTemplate.convertAndSend(exchange, ebaRoutingkey, jsonObj.toString());
    }

    /*@Override
    public Map<String, Integer> getQueuesCounts() {
        Map<String, Integer> queueInfo = new HashMap<String, Integer>();
        List<String> nameList = new ArrayList<>();
        nameList.add("vvSpecial");
        for (String name : nameList) {
            try {
                AMQP.Queue.DeclareOk declareOk = rabbitTemplate.execute(channel -> channel.queueDeclarePassive(name));
                queueInfo.put(name, declareOk.getMessageCount());
            } catch (Exception e) {
                e.printStackTrace();
                queueInfo.put(name, -1);
            }
        }

        return queueInfo;
    }*/
}
