package tr.gov.gib.evdbelge.evdbelgeteblig.dao.impl;

import org.springframework.stereotype.Component;
import tr.gov.gib.evdbelge.evdbelgeteblig.dao.BelgeDaoJpa;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.Belge;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public void belgeUpdate(long belgeId, short durum) throws GibException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Belge> query = criteriaBuilder
                .createCriteriaUpdate(Belge.class);
        Root<Belge> root = query.from(Belge.class);
        List<Predicate> predicates = new ArrayList<>();

        query.set(root.get("durum"), durum);
        predicates.add(criteriaBuilder.equal(root.get("id"), belgeId));
        predicates.add(criteriaBuilder.or(criteriaBuilder.lessThan(root.get("durum"), 100), criteriaBuilder.greaterThanOrEqualTo(root.get("durum"), 102)));
        query.where(predicates.toArray(new Predicate[] {}));
        int checkZero = entityManager.createQuery(query).executeUpdate();

        if(checkZero == 0)
            throw new GibRemoteException("Güncellenen kayıt sayısı 0(sıfır).", "");
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
