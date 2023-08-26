package tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PostScriptSonuc")
public class PostScriptResponse {
    private byte[] psFile;
}
