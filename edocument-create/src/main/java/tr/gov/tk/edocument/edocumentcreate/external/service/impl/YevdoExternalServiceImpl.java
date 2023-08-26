package tr.gov.gib.evdbelge.evdbelgehazirlama.external.service.impl;

import co.elastic.apm.api.CaptureSpan;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgehazirlama.external.service.YevdoExternalService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.external.service.enums.EnumEVDO;
import tr.gov.gib.evdbelge.evdbelgehazirlama.message.Messages;
import tr.gov.gib.gibos.remoteCall.exception.UserError;
import tr.gov.gib.gibos.remoteCall.service.RemoteCallService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

@Service
public class YevdoExternalServiceImpl implements YevdoExternalService, EnumEVDO {
    private final RemoteCallService remoteCallService;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    @Autowired
    public YevdoExternalServiceImpl(RemoteCallService remoteCallService) {
        this.remoteCallService = remoteCallService;
    }

    @CaptureSpan(value = "evdb-islenecekBelgeleriGetir(evdoEBelgeServisGirisi_getIslenecekVeri)", type = "evdb-islenecekBelgeleriGetir(evdoEBelgeServisGirisi_getIslenecekVeri)")
    @Override
    public JsonObject islenecekBelgeleriGetir(int limit) throws GibException {
        JsonObject rcInp = new JsonObject();
        rcInp.addProperty(IslenecekBelgeleriGetir.INPUT.LIMIT.value(), limit);
        try {
            return remoteCallService.remoteCall("yevdoro-service", "evdbelge_server", "dispatch", IslenecekBelgeleriGetir.INPUT.RC_SERVICE_NAME.value(), rcInp)
                    .get(IslenecekBelgeleriGetir.OUTPUT.DATA.value())
                    .getAsJsonObject();
        } catch (UserError e) {
            String message = e.getArgs()[0].toString();
            LOGGER.error(message, e);
            throw new GibRemoteException(e.getArgs()[0].toString(), "");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GibRemoteException(Messages.HATA.message(e.getMessage()), "");
        }
    }
}
