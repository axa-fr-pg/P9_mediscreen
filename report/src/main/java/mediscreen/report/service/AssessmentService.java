package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientRiskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface AssessmentService {

    PatientAssessmentDTO get(PatientData patientData) throws DoctorUnavailableException, JsonProcessingException;
    PatientAssessmentDTO get(long patientId) throws JsonProcessingException, PatientNotFoundException, DoctorUnavailableException;
    PatientAssessmentDTO get(String family) throws PatientNotUniqueException, PatientNotFoundException, JsonProcessingException, DoctorUnavailableException;
    Page<PatientRiskDTO> get(Pageable pageRequest) throws JsonProcessingException, DoctorUnavailableException;
}
