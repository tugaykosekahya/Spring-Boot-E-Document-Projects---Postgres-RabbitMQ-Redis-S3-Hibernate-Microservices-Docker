package tr.gov.gib.evdbelge.evdbelgeteblig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tr.gov.tk")
public class EdocumentcreateApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgeTebligApplication.class, args);
    }

}
