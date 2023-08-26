package tr.gov.gib.evdbelge.evdbelgepdfolusturma.object;


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
    private String memur;
    private String sef;
    private String muduryrd;
    private String mudur;
}
