package tr.gov.gib.evdbelge.evdbelgepdfimzalama.dao.impl;

import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.dao.BelgeDaoJpa;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.Belge;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class BelgeDaoJpaImpl implements BelgeDaoJpa {
    @PersistenceContext(unitName = "postgreEvdbelge")
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List nativeSorgu(String queryString) {
        /*Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        List<Tuple> resultList = query.getResultList();*/

        Query query = entityManager.createNativeQuery(queryString);
        org.hibernate.query.Query nativeQuery = (org.hibernate.query.Query) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List resultList = nativeQuery.getResultList();
        return resultList;
    }

    @Override
    public void belgeUpdate(long belgeId, int durum) throws GibException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Belge> query = criteriaBuilder
                .createCriteriaUpdate(Belge.class);
        Root<Belge> root = query.from(Belge.class);
        List<Predicate> predicates = new ArrayList<>();

        query.set(root.get("durum"), durum);
        predicates.add(criteriaBuilder.equal(root.get("id"), belgeId));
        predicates.add(criteriaBuilder.equal(root.get("durum"), 101));
        query.where(predicates.toArray(new Predicate[] {}));
        int checkZero = entityManager.createQuery(query).executeUpdate();

        if(checkZero == 0)
            throw new GibException("Güncellenen kayıt sayısı 0(sıfır).", "");
    }

    /*@Override
    public List<Belge> sorgu2(String type) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Belge> query = criteriaBuilder.createQuery(Belge.class);
        Root<Belge> root = query.from(Belge.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.greaterThan(criteriaBuilder.function("jsonb_extract_path_text",
                        String.class,
                        root.get("metadata"),
                        criteriaBuilder.literal("VABSUM")).as(BigDecimal.class), new BigDecimal("600.00")));
        query.select(root).where(predicates.toArray(new Predicate[]{})).distinct(true);
        return entityManager.createQuery(query).getResultList();
    }*/
}
