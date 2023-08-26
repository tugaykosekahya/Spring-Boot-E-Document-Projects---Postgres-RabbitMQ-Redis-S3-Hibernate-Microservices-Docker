package tr.gov.gib.evdbelge.evdbelgepdfimzalama.consumer;

import co.elastic.apm.api.Traced;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.object.KuyrukInput;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.PdfImzaService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Component
public class PdfImzaConsumer {
    private static final Gson GSON = new Gson();
    private final GibLogger LOGGER_LOG = GibLoggerFactory.getLogger("serverLogFileLogger");
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    private final PdfImzaService pdfImzaService;

    public PdfImzaConsumer(PdfImzaService pdfImzaService) {
        this.pdfImzaService = pdfImzaService;
    }

    @Traced(value = "pdfImzaListener", type = "readMessageBelgePdfImzaQueue")
    @RabbitListener(queues = {"${spring.rabbitmq.evdbelge.pdfImza.queue}"}, concurrency = "${spring.rabbitmq.evdbelge.count}")
    public void readMessageEklenenBorcQueue(String message, Message msg) throws GibException {

        LOGGER_LOG.info(message);
        //ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
        JsonObject readObj = GSON.fromJson(message, JsonObject.class);

        KuyrukInput kuyrukInput = convertToBelge(readObj.getAsJsonObject("jsonData"));
        CreateBelgeInput createBelgeInput = new CreateBelgeInput();
        createBelgeInput.setOrgOid(kuyrukInput.getOrgoid());
        createBelgeInput.setBelgeTuru(kuyrukInput.getBelgeturu());
        createBelgeInput.setBelgeNo(kuyrukInput.getBelgeno());
        createBelgeInput.setBelgeid(kuyrukInput.getBelgeid());
        pdfImzaService.pdfImzala(createBelgeInput);

    }

    public KuyrukInput convertToBelge(JsonObject jsonObject) throws GibException {
        try {
            return GSON.fromJson(jsonObject, KuyrukInput.class);
        } catch (Exception e) {
            LOGGER.error("Json belge inputa dönüştürülürken hata oluştu.", e);
            throw new GibException("Json belge inputa dönüştürülürken hata oluştu.","");
        }
    }
}
