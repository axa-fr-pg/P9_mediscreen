package mediscreen.patient.repository;

import mediscreen.patient.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    List<PatientEntity> findByFamilyAndDob(String family, Date dob);
}
