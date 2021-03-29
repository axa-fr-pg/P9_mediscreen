package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientData get(long patientId) throws JsonProcessingException, PatientNotFoundException;
    PatientData get(String family) throws PatientNotFoundException, PatientNotUniqueException;
    Page<Long> getAllId(Pageable pageRequest);
}
