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
@Table(name = "belge_hedef_sonuc")
@TypeDef(name = "jsonbType",typeClass = JsonBinaryType.class)
public class BelgeHedefSonuc extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, cascade =CascadeType.MERGE)
    @JoinColumn(name = "belge_hedef_id")
    private BelgeHedef belgeHedef;

    @Column(name = "sonuc_oid")
    private String sonucOid;

    @Column(name = "sonuc")
    @Type(type = "jsonbType", parameters = {@org.hibernate.annotations.Parameter(name = "className",
            value = "com.google.gson.JsonObject")})
    private JsonObject sonuc;
}
