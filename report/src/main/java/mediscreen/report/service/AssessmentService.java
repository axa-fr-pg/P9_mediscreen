package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientRiskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface AssessmentService {

    PatientAssessmentDTO get(PatientData patientData) throws DoctorUnavailableException, IOException;
    PatientAssessmentDTO get(long patientId) throws IOException, PatientNotFoundException, DoctorUnavailableException;
    PatientAssessmentDTO get(String family) throws PatientNotUniqueException, PatientNotFoundException, IOException, DoctorUnavailableException;
    Page<PatientRiskDTO> get(Pageable pageRequest) throws IOException, DoctorUnavailableException;
}
