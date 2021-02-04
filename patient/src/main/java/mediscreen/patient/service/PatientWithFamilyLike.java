package mediscreen.patient.service;

import mediscreen.patient.model.PatientEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PatientWithFamilyLike implements Specification<PatientEntity> {

    private String family = null;

    public PatientWithFamilyLike(String family) {
        this.family = family;
    }

    @Override
    public Predicate toPredicate(Root<PatientEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (family == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        }
        return criteriaBuilder.like(root.get("family"), "%" + this.family + "%");
    }
}
