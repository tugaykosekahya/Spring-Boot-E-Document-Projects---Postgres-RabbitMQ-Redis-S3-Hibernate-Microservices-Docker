package tr.gov.gib.evdbelge.evdbelgegoruntuleme.entity;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.config.JsonBinaryType;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "belge")
@TypeDef(name = "jsonbType",typeClass = JsonBinaryType.class)
public class Belge extends BaseEntity {
    @Id
    @GenericGenerator(name = "oid_generator", strategy = "tr.gov.gib.evdbelge.evdbelgegoruntuleme.entity.OidGenerator")
    @GeneratedValue(generator = "oid_generator")
    private String oid;
    @Column(name = "vkn")
    private String vergino;
    @Column(name = "tckn")
    private String tckimlikno;
    @Column(name = "org_oid")
    private String orgoid;
    @Column(name = "belge_no")
    private String belgeno;
    @Column(name = "durum")
    private int durum;
    @Column(name = "belge_turu")
    private int belgeturu;
    @Column(name = "metadata")
    @Type(type = "jsonbType", parameters = {@org.hibernate.annotations.Parameter(name = "className",
            value = "com.google.gson.JsonObject")})
    private JsonObject metadata;
}
