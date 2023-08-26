package tr.gov.gib.evdbelge.evdbelgeteblig.entity;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import tr.gov.gib.evdbelge.evdbelgeteblig.config.JsonBinaryType;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "belge")
@TypeDef(name = "jsonbType",typeClass = JsonBinaryType.class)
@SelectBeforeUpdate(value = true)
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
    @OneToMany(mappedBy = "belgeAkis", cascade = CascadeType.ALL)
    private List<BelgeAkis> belgeAkislar;
    @OneToMany(mappedBy = "belgeHedef", cascade = CascadeType.ALL)
    private List<BelgeHedef> belgeHedefler;
}
