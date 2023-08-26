package tr.gov.gib.evdbelge.evdbelgeaktarma.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "belge_akis")
public class BelgeAkis extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER, cascade =CascadeType.ALL)
    @JoinColumn(name = "belge_id")
    private Belge belge;
    @Column(name = "kullanici_kodu")
    private String kullaniciKodu;
    @Column(name = "usernodeid")
    private String nextnodeuserid;
    @Column(name = "aciklama")
    private String aciklama;
}
