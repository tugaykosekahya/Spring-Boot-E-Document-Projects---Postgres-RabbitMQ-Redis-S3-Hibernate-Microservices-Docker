package tr.gov.gib.evdbelge.evdbelgehazirlama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "tr.gov.tk")
@EnableScheduling
@EnableCaching
public class EdocumentcreateApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgeHazirlaApplication.class, args);
    }

}
