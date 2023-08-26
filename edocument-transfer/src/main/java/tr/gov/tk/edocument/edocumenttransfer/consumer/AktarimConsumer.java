package tr.gov.gib.evdbelge.evdbelgeaktarma.consumer;

import co.elastic.apm.api.Traced;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeaktarma.service.AktarimService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thslogging.LogParameter;
import tr.gov.gib.tahsilat.thslogging.LogUtil;
import tr.gov.gib.tahsilat.thsutils.OidUtil;

@Component
public class AktarimConsumer {
    private static final Gson GSON = new Gson();
    private final GibLogger LOGGER_LOG = GibLoggerFactory.getLogger("serverLogFileLogger");
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    private final AktarimService aktarimService;

    public AktarimConsumer(AktarimService aktarimService) {
        this.aktarimService = aktarimService;
    }

    @Traced(value = "aktarimListener", type = "readMessageBelgeAktarimQueue")
    @RabbitListener(queues = {"${spring.rabbitmq.evdbelge.queue}"}, concurrency = "${spring.rabbitmq.evdbelge.count}")
    public void readMessageEklenenBorcQueue(String message) throws GibException {
        try {
            LogUtil.setLogParam(LogParameter.STATUS, "||-||");
            LogUtil.setLogParam(LogParameter.ISLEM_ID, "||" + OidUtil.getOid());
            LOGGER_LOG.info(message);
            //ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
            JsonObject readObj = GSON.fromJson(message, JsonObject.class);

            Belge belge = convertToBelge(readObj.getAsJsonObject("jsonData"));
            String belgeIslemeKey = belge.getOrgoid() + ";" + belge.getBelgeturu() + ";" + belge.getBelgeno();
            aktarimService.belgeAktar(readObj, belge, belgeIslemeKey);
            aktarimService.islenmisBelgeBildir(belge, belgeIslemeKey);
            LogUtil.setLogParam(LogParameter.STATUS, "||1||");
            LOGGER_LOG.info("Aktarım başarılı.");
            LogUtil.clearLogParams();
        } catch (Exception e) {
            LogUtil.setLogParam(LogParameter.STATUS, "||0||");
            LOGGER_LOG.error("Aktarım başarısız. Hata Detayı: " + e.getMessage());
            LogUtil.clearLogParams();
            throw e;
        } finally {
            LogUtil.clearLogParams();
        }


        /*if (!jsonObject.get("success").getAsBoolean()) {
            logger.error("İşleme yapılırken bir sorun oluştu. odenen_isleme tablosundan hata mesajını görebilirsiniz. PaketOid : " + jsonObject.get("paketOid").getAsString());
        }*/
    }

    public Belge convertToBelge(JsonObject jsonObject) throws GibException {
        try {
            return GSON.fromJson(jsonObject, Belge.class);
        } catch (Exception e) {
            LOGGER.error("Json belgeye dönüştürülürken hata oluştu.", e);
            throw new GibException("Json belgeye dönüştürülürken hata oluştu.","");
        }
    }
}
