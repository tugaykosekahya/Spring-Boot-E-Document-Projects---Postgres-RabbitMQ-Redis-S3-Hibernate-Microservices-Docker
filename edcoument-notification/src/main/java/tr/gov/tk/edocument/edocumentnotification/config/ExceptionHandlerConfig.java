package tr.gov.gib.evdbelge.evdbelgeteblig.config;

import tr.gov.gib.tahsilat.thsexception.handler.GibExceptionHandler;

public class ExceptionHandlerConfig extends GibExceptionHandler {
    @Override
    public void sendNotification(String s, String s1, String s2) {
        // mail vs göndermek için buraya bilgilerme metodu yazilabilir.
    }
}
