package tr.gov.gib.evdbelge.evdbelgepdfolusturma.consumer;

import co.elastic.apm.api.Traced;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.object.KuyrukInput;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.service.PdfService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Component
public class PdfConsumer {
    private static final Gson GSON = new Gson();
    private final GibLogger LOGGER_LOG = GibLoggerFactory.getLogger("serverLogFileLogger");
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    private final PdfService pdfService;

    public PdfConsumer(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @Traced(value = "pdfListener", type = "readMessageBelgePdfQueue")
    @RabbitListener(queues = {"${spring.rabbitmq.evdbelge.pdf.queue}"}, concurrency = "${spring.rabbitmq.evdbelge.count}")
    public void readMessageEklenenBorcQueue(String message) throws GibException {

        LOGGER_LOG.info(message);
        //ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
        JsonObject readObj = GSON.fromJson(message, JsonObject.class);

        KuyrukInput kuyrukInput = convertToBelge(readObj.getAsJsonObject("jsonData"));
        CreateBelgeInput createBelgeInput = new CreateBelgeInput();
        createBelgeInput.setOrgOid(kuyrukInput.getOrgoid());
        createBelgeInput.setBelgeTuru(kuyrukInput.getBelgeturu());
        createBelgeInput.setBelgeNo(kuyrukInput.getBelgeno());
        createBelgeInput.setBelgeid(kuyrukInput.getBelgeid());
        createBelgeInput.setTaslak("0");
        createBelgeInput.setMemur(kuyrukInput.getMemur());
        createBelgeInput.setSef(kuyrukInput.getSef());
        createBelgeInput.setMuduryrd(kuyrukInput.getMuduryrd());
        createBelgeInput.setMudur(kuyrukInput.getMudur());
        pdfService.pdfOlustur(createBelgeInput);

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
