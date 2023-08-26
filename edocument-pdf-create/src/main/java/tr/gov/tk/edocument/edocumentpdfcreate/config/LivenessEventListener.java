package tr.gov.gib.evdbelge.evdbelgepdfolusturma.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LivenessEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LivenessEventListener.class);
    @EventListener
    public void onEvent(AvailabilityChangeEvent<LivenessState> event) {
        switch (event.getState()) {
            case BROKEN -> LOGGER.error("Liveness: " + "BROKEN");
            case CORRECT -> LOGGER.info("Liveness: " + "CORRECT");
        }
    }
}
