package tr.gov.gib.evdbelge.evdbelgehazirlama.service.impl;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgehazirlama.external.service.YevdoExternalService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BelgeAktarimService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BelgeHazirlamaService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import java.util.concurrent.CompletableFuture;

@Component
public class BelgeHazirlamaServiceImpl implements BelgeHazirlamaService {
    private final YevdoExternalService yevdoExternalService;
    private final BelgeAktarimService belgeAktarimService;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger("serverLogFileLogger");

    public BelgeHazirlamaServiceImpl(YevdoExternalService yevdoExternalService, BelgeAktarimService belgeAktarimService) {
        this.yevdoExternalService = yevdoExternalService;
        this.belgeAktarimService = belgeAktarimService;
    }

    @SneakyThrows
    @Override
    @Async("executer")
    public CompletableFuture<Boolean> belgeGetirVeIsle(int limit) throws GibException {
        JsonObject jsonObject;
        try {
            jsonObject = yevdoExternalService.islenecekBelgeleriGetir(limit);
        } catch (Exception e) {
            LOGGER.error("EVDO_BELGE_HATA: " + e.getMessage());
            jsonObject = null;
        }
        Thread.sleep(10);
        if(jsonObject == null || jsonObject.getAsJsonArray("resultlist") == null || jsonObject.getAsJsonArray("resultlist").size() == 0) {
            if(jsonObject != null) {
                LOGGER.info("EVDO_BELGE_SONUC: " + jsonObject.toString());
            }
            LOGGER.info("Aktarılacak belge: 0");
            LOGGER.info("EVDO_BELGE_SONUC: Belge yok.");
            return CompletableFuture.completedFuture(false);
        }
        LOGGER.info("Aktarılacak belge: " + jsonObject.getAsJsonArray("resultlist").size());
        LOGGER.info("EVDO_BELGE_SONUC: " + jsonObject.toString());
        for (int i = 0; i < jsonObject.getAsJsonArray("resultlist").size(); i++) {
            belgeAktarimService.kuyrukVeRediseAt(jsonObject.getAsJsonArray("resultlist").get(i).getAsJsonObject());
        }
        return CompletableFuture.completedFuture(true);
    }
}
