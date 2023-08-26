package tr.gov.gib.evdbelge.evdbelgehazirlama.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tr.gov.gib.evdbelge.evdbelgehazirlama.entity.Parametreler;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;

import java.util.ArrayList;

@Repository
public interface ParametrelerDao extends GibJPABaseDao<Parametreler, String> {
    ArrayList<Parametreler> findByNameOrName(String name1, String name2);
    ArrayList<Parametreler> findByName(String name1);
    @Query(value = "select * from parametreler limit 1", nativeQuery = true)
    Parametreler getFirst();
}
