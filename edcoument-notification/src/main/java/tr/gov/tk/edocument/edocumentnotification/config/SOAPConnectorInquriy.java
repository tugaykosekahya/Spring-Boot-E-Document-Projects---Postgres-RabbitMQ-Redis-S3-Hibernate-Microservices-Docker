package tr.gov.gib.evdbelge.evdbelgeteblig.config;

import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class SOAPConnectorInquriy extends WebServiceGatewaySupport {

    public Object callWebService(String url, Object request, WebServiceMessageCallback requestCallback){
        return getWebServiceTemplate().marshalSendAndReceive(url, request, requestCallback);
    }
}
