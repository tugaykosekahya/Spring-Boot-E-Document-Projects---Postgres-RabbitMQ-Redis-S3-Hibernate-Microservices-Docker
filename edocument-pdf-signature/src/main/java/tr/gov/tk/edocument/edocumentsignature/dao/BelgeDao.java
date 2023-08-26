package tr.gov.gib.evdbelge.evdbelgepdfimzalama.dao;

import org.springframework.stereotype.Repository;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.Belge;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;

@Repository
public interface BelgeDao extends GibJPABaseDao<Belge, Integer> {
    boolean existsBelgeByBelgenoAndOrgoidAndAndBelgeturu(String belgeno, String orgoid, int belgeturu);
}
