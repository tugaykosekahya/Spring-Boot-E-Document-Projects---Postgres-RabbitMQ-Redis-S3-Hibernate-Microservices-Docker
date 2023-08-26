package tr.gov.gib.evdbelge.evdbelgepdfimzalama.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "basicAuth", scheme = "basic")
@OpenAPIDefinition(
        info = @Info(
                title = "Gelir İdaresi Başkanlığı EVDBELGE Entegrasyon REST API",
                description = "EVDBELGE Entegrasyon servislerinin kullanım açıklamaları ve test arayüzü",
                version = "API 1.0",
                contact = @Contact(
                        name = "İletişim Merkezi",
                        url = "http://www.gib.gov.tr",
                        email = "info@gib.gov.tr"
                )
        ),
        /*externalDocs = @ExternalDocumentation(
                description = "Kullanım Kılavuzu",
                url = "????????"
        ),*/
        security = {
                @SecurityRequirement(name = "basicAuth")
        }
)
public class OpenApiConfig {

    @Value("${spring.profiles.active}")
    private String environment;

    @Value("${api.url}")
    private String serviceUrl;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        List<Server> serverList = new ArrayList<>();
        Server testServer = new Server();
        testServer.setDescription(environment);
        testServer.setUrl(serviceUrl);
        serverList.add(testServer);
        openAPI.setServers(serverList);
        return openAPI;
    }
}