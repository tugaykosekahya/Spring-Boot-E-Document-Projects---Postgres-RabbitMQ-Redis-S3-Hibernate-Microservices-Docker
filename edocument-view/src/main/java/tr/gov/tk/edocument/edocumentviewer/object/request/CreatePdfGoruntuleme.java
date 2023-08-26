package tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request;

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
@Schema(name = "PdfGoruntuleme")
public class CreatePdfGoruntuleme {
    @Schema(description = "CreateSession servisi ile üretilen şifrelenmiş değerdir")
    @Required
    private String session;
}
