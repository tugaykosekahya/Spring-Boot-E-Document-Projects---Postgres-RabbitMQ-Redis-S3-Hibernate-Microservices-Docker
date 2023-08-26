package tr.gov.gib.evdbelge.evdbelgepdfimzalama.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReadinessEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadinessEventListener.class);
    @EventListener
    public void onEvent(AvailabilityChangeEvent<ReadinessState> event) {
        switch (event.getState()) {
            case REFUSING_TRAFFIC -> LOGGER.error("Readiness: " + "REFUSING_TRAFFIC");
            case ACCEPTING_TRAFFIC -> LOGGER.info("Readiness: " + "ACCEPTING_TRAFFIC");
        }
    }
}
