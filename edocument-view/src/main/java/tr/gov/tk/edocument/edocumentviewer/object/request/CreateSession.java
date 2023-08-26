package tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsvalidator.annotations.GibValidator;
import tr.gov.gib.tahsilat.thsvalidator.annotations.Length;
import tr.gov.gib.tahsilat.thsvalidator.annotations.Required;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@GibValidator
@Schema(name = "CreateSession")
public class CreateSession {
    @Schema(description = "OrgOid", example = "00000000001676")
    @Length(from = 14, to = 14)
    @Required
    private String orgOid;

    @Schema(description = "Belge Türü", example = "66")
    @Required
    private String belgeTuru;

    @Schema(description = "T.C. Kimlik Numarası", example = "1998010166Cb90015958")
    @Required
    private String belgeNo;

    private String taslak;
    private String memur;
    private String sef;
    private String muduryrd;
    private String mudur;
}
