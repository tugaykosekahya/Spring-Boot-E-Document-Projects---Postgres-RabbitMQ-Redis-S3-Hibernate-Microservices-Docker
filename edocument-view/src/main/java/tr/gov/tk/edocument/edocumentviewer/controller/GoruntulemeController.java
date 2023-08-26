package tr.gov.gib.evdbelge.evdbelgegoruntuleme.controller;

import co.elastic.apm.api.Traced;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.config.ExceptionHandlerConfig;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.response.PdfResponse;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.response.PostScriptResponse;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.GoruntulemeService;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRequestMapping;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRestController;
import tr.gov.gib.tahsilat.thsbaseobject.GibRequest;
import tr.gov.gib.tahsilat.thsbaseobject.GibResponse;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@GibRestController(path = "")
public class GoruntulemeController extends ExceptionHandlerConfig {

    private final GoruntulemeService goruntulemeService;

    public GoruntulemeController(GoruntulemeService goruntulemeService) {
        this.goruntulemeService = goruntulemeService;
    }

    @Operation(hidden = true)
    @GibRequestMapping(path = "/info", method = RequestMethod.GET)
    GibResponse<String> info(@RequestParam(value = "params", required = false) String params) {
        return GibResponse.<String>builder()
                .data("ok")
                .success()
                .buildInstance();
    }

    @Traced(value = "Pdf Görüntüle", type = "pdfGoruntule")
    @GibRequestMapping(path = "/pdfGoruntule", method = RequestMethod.GET)
    void pdfGoruntule(@RequestParam String session, HttpServletResponse response) throws GibException, IOException {

        byte[] report = goruntulemeService.pdfGoruntule(session, response);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(report);
        outputStream.flush();

    }

    @Traced(value = "Pdf Oluştur", type = "pdfOlustur")
    @GibRequestMapping(path = "/pdfOlustur", method = RequestMethod.POST)
    GibResponse<PdfResponse> pdfOlustur(@RequestBody CreateBelgeInput request) throws GibException, IOException {

        byte[] report = goruntulemeService.pdfOlustur(request);
        PdfResponse pdfResponse = new PdfResponse();
        pdfResponse.setPdfFile(report);

        return GibResponse.<PdfResponse>builder().data(pdfResponse).success().buildInstance();

    }

}
