package tr.gov.gib.evdbelge.evdbelgesorgulama.object.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsbaseobject.BaseDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BelgeSorguSonuc")
public class BelgeSorguResponse extends BaseDTO {
    private List queryResult;
}
