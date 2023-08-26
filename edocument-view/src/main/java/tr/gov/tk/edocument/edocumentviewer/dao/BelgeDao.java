package tr.gov.gib.evdbelge.evdbelgegoruntuleme.dao;

import org.springframework.stereotype.Repository;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.entity.Belge;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;

@Repository
public interface BelgeDao extends GibJPABaseDao<Belge, Integer> {
}
