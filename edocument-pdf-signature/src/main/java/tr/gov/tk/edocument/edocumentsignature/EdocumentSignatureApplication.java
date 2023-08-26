package tr.gov.gib.evdbelge.evdbelgepdfimzalama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tr.gov.tk")
public class EdocumentSignatureApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgePdfImzalamaApplication.class, args);
    }

}
