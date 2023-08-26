package tr.gov.gib.evdbelge.evdbelgeteblig.object.request;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsbaseobject.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BelgeHedefDto extends BaseDTO {
    private long belgeHedefid;
    private long belgeid;
    private String orgoid;
    private short belgeturu;
    private String belgeno;
    private JsonObject metadata;
}
