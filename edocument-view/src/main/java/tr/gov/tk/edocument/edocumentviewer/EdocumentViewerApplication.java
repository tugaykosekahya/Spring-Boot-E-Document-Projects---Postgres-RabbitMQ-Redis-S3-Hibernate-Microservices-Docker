package tr.gov.gib.evdbelge.evdbelgegoruntuleme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "tr.gov.gib",
        exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class EdocumentViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvdbelgeGoruntulemeApplication.class, args);
    }

}
