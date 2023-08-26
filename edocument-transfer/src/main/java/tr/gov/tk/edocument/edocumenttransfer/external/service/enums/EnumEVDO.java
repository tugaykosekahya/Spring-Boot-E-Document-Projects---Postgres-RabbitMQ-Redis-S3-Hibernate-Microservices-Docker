package tr.gov.gib.evdbelge.evdbelgeaktarma.external.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public interface EnumEVDO {
    interface IslenmisBelgeleriBildir {
        @Getter
        @AllArgsConstructor
        enum INPUT {
            RC_SERVICE_NAME("evdoEBelgeServisGirisi_getIslenecekVeriSonuc"),
            ORGOID("ORGOID"),
            BELGENO("BELGENO"),
            ISLEMTURU("ISLEMTURU"),
            ;
            private final String value;
        }

        @Getter
        @AllArgsConstructor
        enum OUTPUT {
            DATA("data"),
            STATUS("status"),
            MSG("msg"),
            ;
            private final String value;
        }
    }
}

