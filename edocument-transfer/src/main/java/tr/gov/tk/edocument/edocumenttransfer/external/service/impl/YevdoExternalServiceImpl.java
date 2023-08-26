package tr.gov.gib.evdbelge.evdbelgeaktarma.external.service.impl;

import co.elastic.apm.api.CaptureSpan;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeaktarma.external.service.YevdoExternalService;
import tr.gov.gib.evdbelge.evdbelgeaktarma.external.service.enums.EnumEVDO;
import tr.gov.gib.evdbelge.evdbelgeaktarma.message.Messages;
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

    @CaptureSpan(value = "evdb-islenmisBelgeBildir(evdoEBelgeServisGirisi_getIslenecekVeriSonuc)", type = "evdb-islenmisBelgeBildir(evdoEBelgeServisGirisi_getIslenecekVeriSonuc)")
    @Override
    public JsonObject islenmisBelgeBildir(Belge belge) throws GibException {
        JsonObject rcInp = new JsonObject();
        rcInp.addProperty(IslenmisBelgeleriBildir.INPUT.ORGOID.value(), belge.getOrgoid());
        rcInp.addProperty(IslenmisBelgeleriBildir.INPUT.BELGENO.value(), belge.getBelgeno());
        rcInp.addProperty(IslenmisBelgeleriBildir.INPUT.ISLEMTURU.value(), belge.getDurum());
        try {
            return remoteCallService.remoteCall("yevdoro-service", "evdbelge_server", "dispatch", IslenmisBelgeleriBildir.INPUT.RC_SERVICE_NAME.value(), rcInp)
                    .get(IslenmisBelgeleriBildir.OUTPUT.DATA.value())
                    .getAsJsonObject();
        } catch (UserError e) {
            String message = e.getArgs()[0].toString();
            LOGGER.error(message, e);
            throw new GibRemoteException(Messages.HATA.message(message), "");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GibRemoteException(Messages.HATA.message(e.getMessage()), "");
        }
    }
}
