package tr.gov.gib.evdbelge.evdbelgepdfolusturma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tr.gov.tk")
public class EdocumentpdfcreateApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgePdfOlusturmaApplication.class, args);
    }

}
