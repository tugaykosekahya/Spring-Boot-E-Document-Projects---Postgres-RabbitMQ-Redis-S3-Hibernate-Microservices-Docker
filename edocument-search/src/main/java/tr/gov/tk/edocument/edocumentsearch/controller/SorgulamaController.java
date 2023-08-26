package tr.gov.gib.evdbelge.evdbelgesorgulama.controller;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Traced;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tr.gov.gib.evdbelge.evdbelgesorgulama.config.ExceptionHandlerConfig;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeAkisGuncelle;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeSorgula;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateGenericInsert;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeAkisGuncelleResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeGenericInsertResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.response.BelgeSorguResponse;
import tr.gov.gib.evdbelge.evdbelgesorgulama.service.BelgeSorgulamaService;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRequestMapping;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRestController;
import tr.gov.gib.tahsilat.thsbaseobject.GibRequest;
import tr.gov.gib.tahsilat.thsbaseobject.GibResponse;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thsexception.custom.GibValidationException;
import tr.gov.gib.tahsilat.thslogging.LogParameter;
import tr.gov.gib.tahsilat.thslogging.LogUtil;

import javax.validation.Valid;

@Validated
@Tag(name = "Belge Sorgulama", description = "Belge Sorgulama ile ilgili servisler bu başlık altında bulunmaktadır.")
@GibRestController(path = "")
public class SorgulamaController extends ExceptionHandlerConfig {
    @Value("${spring.profiles.active}")
    private String ENVIRONMENT;

    private final BelgeSorgulamaService belgeSorgulamaService;

    public SorgulamaController(BelgeSorgulamaService belgeSorgulamaService) {
        this.belgeSorgulamaService = belgeSorgulamaService;
    }

    @Operation(hidden = true)
    @GibRequestMapping(path = "/info", method = RequestMethod.GET)
    GibResponse<String> info(@RequestParam(value = "params", required = false) String params) {
        return GibResponse.<String>builder()
                .data("ok")
                .success()
                .buildInstance();
    }

    @Traced(value = "Belge Sorgula", type = "belgeSorgula")
    @GibRequestMapping(path = "/belgeSorgula")
    GibResponse<BelgeSorguResponse> belgeSorgula(@RequestBody @Valid GibRequest<CreateBelgeSorgula> request) throws GibException {
        ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
        return GibResponse.<BelgeSorguResponse>builder()
                .data(belgeSorgulamaService.belgeSorgula(request.getBody()))
                .success()
                .buildInstance();
    }

    @Traced(value = "Test Generic Insert", type = "testGenericInsert")
    @GibRequestMapping(path = "/testGenericInsert")
    GibResponse<BelgeGenericInsertResponse> testGenericInsert(@RequestBody @Valid GibRequest<CreateGenericInsert> request) throws GibException {
        ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
        if("PROD".equals(ENVIRONMENT)) {
            throw new GibRemoteException("Servis kullanılamaz.", "");
        }
        return GibResponse.<BelgeGenericInsertResponse>builder()
                .data(belgeSorgulamaService.nativeInsert(request.getBody()))
                .success()
                .buildInstance();
    }

    @Traced(value = "Belge Akış Güncelle", type = "belgeAkisGuncelle")
    @GibRequestMapping(path = "/belgeAkisGuncelle")
    GibResponse<BelgeAkisGuncelleResponse> belgeAkisGuncelle(@RequestBody @Valid GibRequest<CreateBelgeAkisGuncelle> request) throws GibException {
        ElasticApm.currentTransaction().setLabel("islemId", LogUtil.getLogParam(LogParameter.ISLEM_ID));
        return GibResponse.<BelgeAkisGuncelleResponse>builder()
                .data(belgeSorgulamaService.akisGuncelle(request.getBody()))
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
}
