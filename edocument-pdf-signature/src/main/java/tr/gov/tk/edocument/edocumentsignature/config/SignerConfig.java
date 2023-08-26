package tr.gov.gib.evdbelge.evdbelgepdfimzalama.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tr.gov.gib.signserver.client.GIBSSClient;
import tr.gov.gib.util.signer.SignerUtil;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;

import java.io.*;

@Configuration
public class SignerConfig {
    @Value("${signer.server.url}")
    public String SIGN_SERVER_URL;
    @Value("${signer.server.clientId}")
    public String SIGN_SERVER_CLIENT_ID;
    @Value("${signer.server.algorithm}")
    public String SIGN_SERVER_ALGORITHM;
    @Value("${signer.zamanDamgasi.url}")
    public String ZAMAN_DAMGASI_URL;
    @Value("${signer.zamanDamgasi.user}")
    public Integer ZAMAN_DAMGASI_USER;
    @Value("${signer.zamanDamgasi.password}")
    public String ZAMAN_DAMGASI_PASSWORD;

    @Bean
    public void setSignProperties() {
        GIBSSClient.setProperties(SIGN_SERVER_URL,
                SIGN_SERVER_CLIENT_ID, SIGN_SERVER_ALGORITHM);
        SignerUtil.setTSSettings(ZAMAN_DAMGASI_URL, ZAMAN_DAMGASI_USER, ZAMAN_DAMGASI_PASSWORD, DigestAlg.SHA256);
    }

}
