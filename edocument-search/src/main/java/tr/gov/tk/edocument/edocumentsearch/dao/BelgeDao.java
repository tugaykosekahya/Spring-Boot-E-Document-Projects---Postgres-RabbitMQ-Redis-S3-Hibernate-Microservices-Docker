package tr.gov.gib.evdbelge.evdbelgesorgulama.dao;

import org.springframework.stereotype.Repository;
import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.Belge;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;

@Repository
public interface BelgeDao extends GibJPABaseDao<Belge, Long> {
    //@Query(value = "select * from evdbelge.belge where CAST((metadata->>'VABSUM') as decimal) > 600", nativeQuery = true)
    //ArrayList<Belge> sorgu1(@Param("type") String type);
}
