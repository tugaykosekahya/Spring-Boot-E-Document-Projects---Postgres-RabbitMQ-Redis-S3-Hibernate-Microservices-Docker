package tr.gov.gib.evdbelge.evdbelgehazirlama.external.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public interface EnumEVDO {
    interface SicilTcdenVknBul {
        @Getter
        @AllArgsConstructor
        enum INPUT {
            RC_SERVICE_NAME("sicilDisModulServis_calistir"),
            EVDO_NAME("EVDO_YSICIL_DISMODUL_TCKNOILEKIMLIKBILGISORGULADETAYSIZ"),
            SERVISADI("SERVISADI"),
            MUKELLEF_NO("mukellefNo");
            private final String value;
        }

        @Getter
        @AllArgsConstructor
        enum OUTPUT {
            DATA("data"),
            VERGI_NO("vergiNo");
            private final String value;
        }
    }

    interface IslenecekBelgeleriGetir {
        @Getter
        @AllArgsConstructor
        enum INPUT {
            RC_SERVICE_NAME("evdoEBelgeServisGirisi_getIslenecekVeri"),
            LIMIT("LIMIT");
            private final String value;
        }

        @Getter
        @AllArgsConstructor
        enum OUTPUT {
            DATA("data"),
            ERROR_LIST("errorList"),
            RESULT_LIST("resultList"),
            METADATA("metadata"),
            RESPONSE("response"),
            ;
            private final String value;
        }
    }
}

