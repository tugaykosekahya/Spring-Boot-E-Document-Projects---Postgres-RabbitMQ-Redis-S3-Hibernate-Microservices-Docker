package tr.gov.gib.evdbelge.evdbelgesorgulama.object.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsbaseobject.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BelgeAkisGuncelleSonuc")
public class BelgeAkisGuncelleResponse extends BaseDTO {
    private String resultMsg;
    private short resultCode;
}
