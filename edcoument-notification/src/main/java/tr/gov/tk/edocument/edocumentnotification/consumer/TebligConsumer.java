package tr.gov.gib.evdbelge.evdbelgeteblig.consumer;

import co.elastic.apm.api.Traced;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgeteblig.object.request.BelgeHedefDto;
import tr.gov.gib.evdbelge.evdbelgeteblig.service.TebligService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thslogging.LogParameter;
import tr.gov.gib.tahsilat.thslogging.LogUtil;
import tr.gov.gib.tahsilat.thsutils.OidUtil;

@Component
public class TebligConsumer {
    private static final Gson GSON = new Gson();
    private final GibLogger LOGGER_LOG = GibLoggerFactory.getLogger("serverLogFileLogger");
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    private final TebligService tebligService;

    public TebligConsumer(TebligService tebligService) {
        this.tebligService = tebligService;
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

            BelgeHedefDto belgeHedefDto = convertToBelgeHedef(readObj.getAsJsonObject("jsonData"));
            String belgeIslemeKey = belgeHedefDto.getOrgoid() + ";" + belgeHedefDto.getBelgeturu() + ";" + belgeHedefDto.getBelgeno();
            tebligService.etebligataGonder(belgeHedefDto, belgeIslemeKey);
            LogUtil.setLogParam(LogParameter.STATUS, "||1||");
            LOGGER_LOG.info("Aktarım başarılı.");
            LogUtil.clearLogParams();
        } catch (Exception e) {
            LogUtil.setLogParam(LogParameter.STATUS, "||0||");
            LOGGER_LOG.error("Aktarım başarısız.", e);
            LogUtil.clearLogParams();
            throw e;
        } finally {
            LogUtil.clearLogParams();
        }
    }

    public BelgeHedefDto convertToBelgeHedef(JsonObject jsonObject) throws GibException {
        try {
            return GSON.fromJson(jsonObject, BelgeHedefDto.class);
        } catch (Exception e) {
            LOGGER.error("Json belge hedefe dönüştürülürken hata oluştu.", e);
            throw new GibException("Json belge hedefe dönüştürülürken hata oluştu.","");
        }
    }
}
