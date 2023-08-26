package tr.gov.gib.evdbelge.evdbelgesorgulama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tr.gov.tk")
public class EdocumentSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgeSorgulamaApplication.class, args);
    }

}
