package mediscreen.patient.repository;

import mediscreen.patient.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository
        extends JpaRepository<PatientEntity, Long>,
        JpaSpecificationExecutor<PatientEntity> {
    List<PatientEntity> findByFamilyAndDob(String family, LocalDate dob);
}
