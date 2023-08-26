package tr.gov.gib.evdbelge.evdbelgesorgulama.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import tr.gov.gib.evdbelge.evdbelgesorgulama.config.InetType;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "belge_akis")
@TypeDef(name = "inetType",typeClass = InetType.class)
public class BelgeAkis extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER, cascade =CascadeType.MERGE)
    @JoinColumn(name = "belge_id")
    private Belge belge;
    @Column(name = "kullanici_kodu")
    private String kullaniciKodu;
    @Column(name = "usernodeid")
    private String nextnodeuserid;
    @Column(name = "userip")
    @Type(type = "inetType")
    private String userip;
    @Column(name = "aciklama")
    private String aciklama;
    @OneToMany(mappedBy = "belgeAkis", cascade = CascadeType.ALL)
    private List<BelgeHedef> belgeHedefler;
}
