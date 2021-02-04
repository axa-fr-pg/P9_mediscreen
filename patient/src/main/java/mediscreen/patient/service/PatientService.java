package mediscreen.patient.service;

import mediscreen.patient.model.PatientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    List<PatientDTO> getList();
    Page<PatientDTO> getPage(Pageable pageRequest, String patientId, String family, String dob);
    PatientDTO get(long patientId) throws PatientNotFoundException;
    PatientDTO put(PatientDTO patient) throws PatientNotFoundException;
    PatientDTO post(PatientDTO patient) throws CreateExistingPatientException;
    List<PatientDTO> post(int numberOfRows);
}
