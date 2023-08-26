package tr.gov.gib.evdbelge.evdbelgesorgulama.object.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsvalidator.annotations.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@GibValidator
@Schema(name = "BelgeSorgu")
public class CreateBelgeSorgula {
    @Schema(description = "Sql script stringi", example = "select * from belge limit 1")
    @Required
    private String query;
}
