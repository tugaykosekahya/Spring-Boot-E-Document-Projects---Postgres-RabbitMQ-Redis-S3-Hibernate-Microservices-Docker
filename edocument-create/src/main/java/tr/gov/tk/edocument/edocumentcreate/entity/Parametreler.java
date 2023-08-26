package tr.gov.gib.evdbelge.evdbelgehazirlama.entity;

import lombok.Getter;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsbasedao.object.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "parametreler")
public class Parametreler extends BaseEntity {
    @Id
    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private String value;
}
