package tr.gov.gib.evdbelge.evdbelgehazirlama.scheduler;

import co.elastic.apm.api.Traced;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgehazirlama.cache.ParametrelerCache;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BakimService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BelgeHazirlamaService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.enums.EnumParametreler;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EnableScheduling
@Component
public class AktarimScheduler {
    private final BelgeHazirlamaService belgeHazirlamaService;
    private final BakimService bakimService;
    private final ParametrelerCache parametrelerCache;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger("serverLogFileLogger");
    private final static String KONTROL_BELGE_OKUMA_LIMIT = "1";
    private final static String KONTROL_THREAD_SIZE = "1";
    private static boolean BEKLE_FLAG = false;

    public AktarimScheduler(BelgeHazirlamaService belgeHazirlamaService, BakimService bakimService, ParametrelerCache parametrelerCache) {
        this.belgeHazirlamaService = belgeHazirlamaService;
        this.bakimService = bakimService;
        this.parametrelerCache = parametrelerCache;
    }

    @SneakyThrows
    @Traced(value = "scheduler-aktarim", type = "belgeGetirVeIsle")
    //@Scheduled(cron = "0/10 * * * * *", zone = "Europe/Istanbul")
    @Scheduled(fixedRate = 100)
    void run() throws GibException {
        bakimService.bakimYogunlukKontrol();
        LOGGER.info("Zamanlayıcı başladı.");
        try {
            //Thread.sleep(10000);
            String limit;
            String threadSize;
            if(BEKLE_FLAG) {
                limit = KONTROL_BELGE_OKUMA_LIMIT;
                threadSize = KONTROL_THREAD_SIZE;
            }
            else {
                limit = parametrelerCache.getParametreByKey(EnumParametreler.BELGE_OKUMA_LIMIT);
                threadSize = parametrelerCache.getParametreByKey(EnumParametreler.THREAD_SIZE);
            }
            if(limit == null || limit.equals("0") || threadSize == null) {
                threadSize = "0";
            }
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(threadSize); i++) {
                futures.add(belgeHazirlamaService.belgeGetirVeIsle(Integer.parseInt(limit)));
            }
            BEKLE_FLAG = false;
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            for (CompletableFuture<Boolean> future : futures) {
                if (!future.get()) {
                    BEKLE_FLAG = true;
                    break;
                }
            }
            if(BEKLE_FLAG) {
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            LOGGER.error("Hata: " + e.getMessage());
        } finally {
            LOGGER.info("Zamanlayıcı bitti.");
        }
    }
}
