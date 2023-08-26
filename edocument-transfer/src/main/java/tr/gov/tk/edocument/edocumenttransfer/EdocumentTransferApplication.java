package tr.gov.gib.evdbelge.evdbelgeaktarma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tr.gov.gib")
public class EdocumentTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgeAktarmaApplication.class, args);
    }

}
