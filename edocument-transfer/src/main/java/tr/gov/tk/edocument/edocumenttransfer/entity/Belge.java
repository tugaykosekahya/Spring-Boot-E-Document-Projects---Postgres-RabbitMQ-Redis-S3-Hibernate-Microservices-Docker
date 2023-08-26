package tr.gov.gib.evdbelge.evdbelgeaktarma.entity;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import tr.gov.gib.evdbelge.evdbelgeaktarma.config.JsonBinaryType;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "belge")
@TypeDef(name = "jsonbType",typeClass = JsonBinaryType.class)
public class Belge extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "vkn")
    private String vergino;
    @Column(name = "tckn")
    private String tckimlikno;
    @Column(name = "org_oid")
    private String orgoid;
    @Column(name = "belge_no")
    private String belgeno;
    @Column(name = "durum")
    private short durum;
    @Column(name = "belge_turu")
    private short belgeturu;
    @Column(name = "metadata")
    @Type(type = "jsonbType", parameters = {@org.hibernate.annotations.Parameter(name = "className",
            value = "com.google.gson.JsonObject")})
    private JsonObject metadata;
    @Column(name = "usernodeid")
    private String nextnodeuserid;
    @OneToMany(mappedBy = "belge", cascade = CascadeType.ALL)
    private List<BelgeAkis> belgeAkislar;

    @Transient
    private String kullanicikodu;
}
