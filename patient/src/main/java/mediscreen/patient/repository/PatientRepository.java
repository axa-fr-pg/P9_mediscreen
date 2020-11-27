package mediscreen.patient.repository;

import mediscreen.patient.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    List<PatientEntity> findByFamilyAndDob(String family, LocalDate dob);
}
