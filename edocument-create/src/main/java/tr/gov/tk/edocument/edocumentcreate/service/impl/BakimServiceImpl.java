package tr.gov.gib.evdbelge.evdbelgehazirlama.service.impl;

import co.elastic.apm.api.CaptureSpan;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgehazirlama.cache.ParametrelerCache;
import tr.gov.gib.evdbelge.evdbelgehazirlama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BakimService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.enums.EnumParametreler;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Component
public class BakimServiceImpl implements BakimService {
    private static final String YOGUNLUK_VAR = "2";
    private static final String BAKIM_VAR = "1";
    private static final String BAKIM_YOK = "0";
    private final ParametrelerCache parametrelerCache;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BakimServiceImpl(ParametrelerCache parametrelerCache) {
        this.parametrelerCache = parametrelerCache;
    }

    public void bakimYogunlukKontrol() throws GibException {
        bakimVeYogunlukDurumuKontrol();
    }

    @CaptureSpan(value = "bakimVeYogunlukDurumuKontrol", type = "bakimVeYogunlukDurumuKontrol")
    @SneakyThrows
    public void bakimVeYogunlukDurumuKontrol() throws GibException {
        try {
            String key = parametrelerCache.getParametreByKey(EnumParametreler.BAKIM);
            if(StringUtils.isNotEmpty(key)) {
                if(BAKIM_VAR.equals(key)) {
                    Thread.sleep(10000);
                    throw new GibBusinessException(Messages.BAKIM_MESAJ.message(), "");
                }
                if(YOGUNLUK_VAR.equals(key)) {
                    Thread.sleep(10000);
                    throw new GibBusinessException(Messages.YOGUNLUK_MESAJ.message(), "");
                }
            }
            else {
                throw new GibRemoteException(Messages.REDIS_KEY_HATASI.message(EnumParametreler.BAKIM.parametre()), "");
            }
        } catch (GibBusinessException e) {
            throw e;
        } catch (GibRemoteException e) {
            LOGGER.error(Messages.REDIS_KEY_HATASI.message(EnumParametreler.BAKIM.parametre()), e);
            throw new GibRemoteException(Messages.REDIS_KEY_HATASI.message(EnumParametreler.BAKIM.parametre()), "");
        }
    }
}
