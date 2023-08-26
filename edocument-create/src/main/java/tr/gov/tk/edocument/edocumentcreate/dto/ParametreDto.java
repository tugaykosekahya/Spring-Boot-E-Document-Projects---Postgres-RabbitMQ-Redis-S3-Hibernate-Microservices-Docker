package tr.gov.gib.evdbelge.evdbelgehazirlama.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsbaseobject.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametreDto extends BaseDTO {
    private String hazirlamaBakim;
}
