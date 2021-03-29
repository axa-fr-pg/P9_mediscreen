package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;

public interface AssessmentService {
    PatientAssessmentDTO getByPatient(PatientData patientData) throws DoctorUnavailableException, JsonProcessingException;
}
