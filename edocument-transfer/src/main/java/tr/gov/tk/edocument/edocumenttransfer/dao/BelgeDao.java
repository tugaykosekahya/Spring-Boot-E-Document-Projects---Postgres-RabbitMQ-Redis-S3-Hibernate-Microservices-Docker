package tr.gov.gib.evdbelge.evdbelgeaktarma.dao;

import org.springframework.stereotype.Repository;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;

@Repository
public interface BelgeDao extends GibJPABaseDao<Belge, Long> {
    boolean existsByOrgoidAndBelgenoAndAndBelgeturu(String orgoid, String belgeno, short belgeturu);
    Belge findByOrgoidAndBelgenoAndAndBelgeturu(String orgoid, String belgeno, short belgeturu);
}
