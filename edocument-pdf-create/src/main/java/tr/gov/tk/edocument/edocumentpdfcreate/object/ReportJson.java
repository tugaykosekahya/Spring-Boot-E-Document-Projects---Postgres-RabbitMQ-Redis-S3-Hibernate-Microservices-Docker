package tr.gov.gib.evdbelge.evdbelgepdfolusturma.object;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportJson {
    private HashMap<String, Object> reportDetail;
    private JsonObject reportData;
}
