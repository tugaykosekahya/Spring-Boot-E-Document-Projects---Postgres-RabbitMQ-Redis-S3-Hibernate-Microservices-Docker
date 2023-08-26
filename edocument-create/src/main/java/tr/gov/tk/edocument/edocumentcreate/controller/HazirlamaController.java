package tr.gov.gib.evdbelge.evdbelgehazirlama.controller;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Traced;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tr.gov.gib.evdbelge.evdbelgehazirlama.config.ExceptionHandlerConfig;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BelgeAktarimService;
import tr.gov.gib.evdbelge.evdbelgehazirlama.service.BelgeHazirlamaService;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRequestMapping;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRestController;
import tr.gov.gib.tahsilat.thsbaseobject.GibResponse;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.LogParameter;
import tr.gov.gib.tahsilat.thslogging.LogUtil;
import tr.gov.gib.tahsilat.thsvalidator.annotations.Range;
import tr.gov.gib.tahsilat.thsvalidator.annotations.Required;

@Validated
@Tag(name = "Belge Hazırlama", description = "Belge Hazırlama ile ilgili servisler bu başlık altında bulunmaktadır.")
@GibRestController(path = "")
public class HazirlamaController extends ExceptionHandlerConfig {
    private final BelgeHazirlamaService belgeHazirlamaService;
    private final BelgeAktarimService belgeAktarimService;

    public HazirlamaController(BelgeHazirlamaService belgeHazirlamaService, BelgeAktarimService belgeAktarimService) {
        this.belgeHazirlamaService = belgeHazirlamaService;
        this.belgeAktarimService = belgeAktarimService;
    }

    @Operation(hidden = true)
    @GibRequestMapping(path = "/info", method = RequestMethod.GET)
    GibResponse<String> info(@RequestParam(value = "params", required = false) String params) {
        return GibResponse.<String>builder()
                .data("ok")
                .success()
                .buildInstance();
    }

    @Traced(value = "Belge Getir Ve İşle", type = "belgeGetirVeIsle")
    @GibRequestMapping(path = "/belgeGetirVeIsle", method = RequestMethod.GET)
    GibResponse<String> belgeGetirVeIsle(@RequestParam(value = "params", required = false) @Required @Range(from = "1", to = "1000", compareClass = String.class)String params) throws GibException {
        ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
        belgeHazirlamaService.belgeGetirVeIsle(Integer.parseInt(params));
        return GibResponse.<String>builder()
                .data("ok")
                .success()
                .buildInstance();
    }

    @Operation(hidden = true)
    @GibRequestMapping(path = "/monitorRequest", method = RequestMethod.GET)
    GibResponse<String> monitorRequest(@RequestParam(value = "params", required = false) String params) throws GibException {
        //todo monitor service yazilacak.
        return GibResponse.<String>builder()
                .data("ok")
                .success()
                .buildInstance();
    }

    @Operation(hidden = true)
    @GibRequestMapping(path = "/queueRequest", method = RequestMethod.GET)
    GibResponse<String> queueRequest(@RequestParam(value = "params", required = false) String params) throws GibException {
        return GibResponse.<String>builder()
                .data(belgeAktarimService.queueRequest())
                .success()
                .buildInstance();
    }
}
