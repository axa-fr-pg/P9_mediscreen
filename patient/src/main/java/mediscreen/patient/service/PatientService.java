package mediscreen.patient.service;

import mediscreen.patient.model.PatientDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    List<PatientDTO> getList();
    Page<PatientDTO> getPageSortById(int pageNumber);
    PatientDTO get(long patientId) throws PatientNotFoundException;
    PatientDTO put(PatientDTO patient) throws PatientNotFoundException;
    PatientDTO post(PatientDTO patient) throws CreateExistingPatientException;
    List<PatientDTO> post(int numberOfRows);
}
