package tr.gov.gib.evdbelge.evdbelgeteblig.dao;

import org.springframework.stereotype.Repository;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedef;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;

@Repository
public interface BelgeHedefDao extends GibJPABaseDao<BelgeHedef, Long> {
    boolean existsBelgeHedefByBelgeHedef(Belge belgeHedef);
}
