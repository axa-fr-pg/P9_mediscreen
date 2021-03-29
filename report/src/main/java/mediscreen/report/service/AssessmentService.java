package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;

public interface AssessmentService {

    PatientAssessmentDTO get(PatientData patientData) throws DoctorUnavailableException, JsonProcessingException;
    PatientAssessmentDTO get(long patientId) throws JsonProcessingException, PatientNotFoundException, DoctorUnavailableException;
    PatientAssessmentDTO get(String family) throws PatientNotUniqueException, PatientNotFoundException, JsonProcessingException, DoctorUnavailableException;
}
