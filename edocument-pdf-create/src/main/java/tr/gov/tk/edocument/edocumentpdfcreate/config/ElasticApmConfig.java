package tr.gov.gib.evdbelge.evdbelgepdfolusturma.config;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ElasticApmConfig {
    @Value("${elastic.apm.service-name}")
    private String serviceName;

    @Value("${elastic.apm.application-packages}")
    private String applicationPackages;

    @Value("${elastic.apm.server-url}")
    private String serverUrl;

    @Value("${elastic.apm.secret-token}")
    private String secretToken;

    @Value("${elastic.apm.verify-server-cert}")
    private String verifyServerCert;

    @Value("${elastic.apm.enable}")
    private boolean enable;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if(enable) {
            Map<String, String> configuration = new HashMap<>();
            configuration.put("service_name", serviceName);
            configuration.put("application_packages", applicationPackages);
            configuration.put("server_urls", serverUrl);
            configuration.put("secret_token", secretToken);
            configuration.put("verify_server_cert", verifyServerCert);
            ElasticApmAttacher.attach(configuration);
        }
    }
}

