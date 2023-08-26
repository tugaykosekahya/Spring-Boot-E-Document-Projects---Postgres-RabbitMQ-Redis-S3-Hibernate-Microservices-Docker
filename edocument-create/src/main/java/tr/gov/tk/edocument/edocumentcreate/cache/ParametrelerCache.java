package tr.gov.gib.evdbelge.evdbelgehazirlama.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgehazirlama.entity.Parametreler;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.ParametrelerService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.enums.EnumParametreler;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ParametrelerCache {
    private static final ConcurrentHashMap<String, String> parametrelerMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> parametrelerTempMap = new ConcurrentHashMap<>();
    private final ParametrelerService parametrelerService;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public ParametrelerCache(ParametrelerService parametrelerService) {
        this.parametrelerService = parametrelerService;
    }

    @Value("${api.name}")
    private String appName;

    @Cacheable(value = "parametreler")
    public ConcurrentHashMap<String, String> getParametrelerMap() {
        return parametrelerMap;
    }

    @Bean()
    public void createParametrelerCache() throws GibException {
        EnumParametreler.setAppName(appName);
        refresh();
    }

    @Scheduled(cron = "0 * * * * *", zone = "Europe/Istanbul")
    public void evictAllCacheValues() throws GibException {
        refresh();
        evictParameter();
        System.out.println("parametrelerCache cache yenilendi.");
    }

    @CacheEvict(value = "parametreler")
    public void evictParameter() {
        System.out.println("parametreler cache temizlendi.");
    }

    public void refresh() throws GibException {
        try {
            List<Parametreler> paramListDB = parametrelerService.getAllParametrelerFromDB();
            List<String> nameListDB = paramListDB.stream().map(Parametreler::getName).toList();
            List<String> valueListDB = paramListDB.stream().map(Parametreler::getValue).toList();
            List<String> paramListRedis = new ArrayList<>();
            try {
                paramListRedis = parametrelerService.getCanliParametreFromRedis(nameListDB.toArray(String[]::new));
            } catch (Exception e) {
                LOGGER.warn("Bakım yoğunluk durumu veri tabanından kontrol edilecek.");
            }
            for (int i = 0; i < nameListDB.size(); i++) {
                if(paramListRedis.size() > 0 && StringUtils.isNotEmpty(paramListRedis.get(i))) {
                    parametrelerTempMap.put(nameListDB.get(i), paramListRedis.get(i));
                }
                else {
                    parametrelerTempMap.put(nameListDB.get(i), valueListDB.get(i));
                    if(!parametrelerMap.contains(nameListDB.get(i))) {
                        parametrelerService.putParametreToRedis(nameListDB.get(i), valueListDB.get(i));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("parametrelerCache oluşturulurken hata.", e);
            throw e;
        }
        parametrelerMap.clear();
        parametrelerMap.putAll(parametrelerTempMap);
        LOGGER.info("parametrelerCache oluşturuldu.");
        parametrelerTempMap.clear();
    }

    public String getParametreByKey(String key) {
        return getParametrelerMap().get(key);
    }

    public String getParametreByKey(EnumParametreler key) {
        return getParametrelerMap().get(key.parametre());
    }

}
