package tr.gov.gib.evdbelge.evdbelgepdfimzalama.object.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsvalidator.annotations.GibValidator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@GibValidator
@Schema(name = "BodyInput")
public class BodyInput {
    CreateBelgeInput body;
}
