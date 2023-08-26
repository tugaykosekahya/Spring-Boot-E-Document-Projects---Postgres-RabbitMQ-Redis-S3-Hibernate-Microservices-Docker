package tr.gov.gib.evdbelge.evdbelgeteblig.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class WebClientConfig {

    @Value("${etebligat.server.url}")
    private String etebligatUrl;

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return messageFactory;
    }
    @Bean
    public SOAPConnectorInquriy soapConnectorInquriy() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("tr.gov.gib.evdbelge.evdbelgeteblig.client.codes");
        SOAPConnectorInquriy client = new SOAPConnectorInquriy();
        client.setDefaultUri(etebligatUrl);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        //client.setMessageSender(defaultMwMessageSenderInquriy());
        //client.setMessageFactory(messageFactory());
        return client;
    }

   /* @Bean
    public HttpComponentsMessageSender defaultMwMessageSenderInquriy() {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setCredentials(new UsernamePasswordCredentials(edevletServiceUsername, edevletServicePassword));
        return messageSender;
    }*/

}
