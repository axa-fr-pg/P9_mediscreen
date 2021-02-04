package mediscreen.patient.service;

import mediscreen.patient.model.PatientEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientWithDobEqual implements Specification<PatientEntity> {

    private String dob = null;

    public PatientWithDobEqual(String family) {
        this.dob = family;
    }

    @Override
    public Predicate toPredicate(Root<PatientEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (dob == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(this.dob, formatter);
        } catch (DateTimeParseException exception) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(false)); // always false = filtering all rows
        }
        return criteriaBuilder.equal(root.get("dob"), LocalDate.parse(this.dob, formatter));
    }
}
