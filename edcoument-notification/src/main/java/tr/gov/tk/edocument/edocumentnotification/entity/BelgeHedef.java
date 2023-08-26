package tr.gov.gib.evdbelge.evdbelgeteblig.entity;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import tr.gov.gib.evdbelge.evdbelgeteblig.config.JsonBinaryType;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "belge_hedef")
@TypeDef(name = "jsonbType",typeClass = JsonBinaryType.class)
public class BelgeHedef extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER, cascade =CascadeType.MERGE)
    @JoinColumn(name = "belge_id")
    private Belge belgeHedef;
    @Column(name = "hedef_id")
    private short hedef;
    @ManyToOne(fetch = FetchType.EAGER, cascade =CascadeType.MERGE)
    @JoinColumn(name = "belge_akis_id")
    private BelgeAkis belgeAkis;
    @Column(name = "metadata")
    @Type(type = "jsonbType", parameters = {@org.hibernate.annotations.Parameter(name = "className",
            value = "com.google.gson.JsonObject")})
    private JsonObject metadata;
    @OneToOne(mappedBy = "belgeHedef", cascade = CascadeType.ALL)
    private BelgeHedefSonuc belgeHedefSonuc;
}
