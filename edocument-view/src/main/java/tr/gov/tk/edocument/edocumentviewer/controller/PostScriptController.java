package tr.gov.gib.evdbelge.evdbelgegoruntuleme.controller;

import co.elastic.apm.api.Traced;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.config.ExceptionHandlerConfig;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.response.PostScriptResponse;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.PostScriptService;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRequestMapping;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRestController;
import tr.gov.gib.tahsilat.thsbaseobject.GibResponse;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

@GibRestController(path = "")
public class PostScriptController extends ExceptionHandlerConfig {
    private final PostScriptService postScriptService;

    public PostScriptController(PostScriptService postScriptService) {
        this.postScriptService = postScriptService;
    }


    @Traced(value = "Ps oluştur", type = "psOlustur")
    @GibRequestMapping(path = "/psOlustur", method = RequestMethod.GET)
    GibResponse<PostScriptResponse> psOlustur(@RequestParam String session) throws GibException {

        byte[] psFile = postScriptService.makePSFile(session);
        PostScriptResponse postScriptResponse = new PostScriptResponse();
        postScriptResponse.setPsFile(psFile);
        return GibResponse.<PostScriptResponse>builder().data(postScriptResponse).success().buildInstance();

    }
}
