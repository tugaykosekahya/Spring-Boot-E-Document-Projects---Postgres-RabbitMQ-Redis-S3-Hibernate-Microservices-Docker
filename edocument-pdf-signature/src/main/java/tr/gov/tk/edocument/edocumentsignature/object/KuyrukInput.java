package tr.gov.gib.evdbelge.evdbelgepdfimzalama.object;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KuyrukInput {
    private long belgeid;
    private String orgoid;
    private String belgeturu;
    private String belgeno;
    private String optime;
}
