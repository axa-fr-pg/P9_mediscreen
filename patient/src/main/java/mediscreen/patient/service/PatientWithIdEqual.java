package mediscreen.patient.service;

import mediscreen.patient.model.PatientEntity;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PatientWithIdEqual implements Specification<PatientEntity> {

    private String id = null;

    public PatientWithIdEqual(String id) {
        this.id = id;
    }

    @Override
    public Predicate toPredicate(Root<PatientEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (id == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        }
        return criteriaBuilder.equal(root.get("id"), NumberUtils.toLong(this.id));
    }
}
