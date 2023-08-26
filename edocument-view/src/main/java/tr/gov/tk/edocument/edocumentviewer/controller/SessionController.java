package tr.gov.gib.evdbelge.evdbelgegoruntuleme.controller;

import co.elastic.apm.api.Traced;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.config.ExceptionHandlerConfig;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateSession;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.SessionService;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRequestMapping;
import tr.gov.gib.tahsilat.thsbasecontroller.annotations.GibRestController;
import tr.gov.gib.tahsilat.thsbaseobject.GibRequest;
import tr.gov.gib.tahsilat.thsbaseobject.GibResponse;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@GibRestController(path = "")
public class SessionController extends ExceptionHandlerConfig {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Traced(value = "Session oluştur", type = "createSession")
    @GibRequestMapping(path = "/createSession", method = RequestMethod.POST)
    GibResponse<String> createSession(@RequestBody @Valid GibRequest<CreateSession> request) throws UnsupportedEncodingException {

        String session = sessionService.createSession(request.getBody());
        session = URLEncoder.encode(session, StandardCharsets.UTF_8.name());

        return GibResponse.<String>builder().data(session).success().buildInstance();
    }

}
