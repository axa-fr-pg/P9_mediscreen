package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientData;

public interface PatientService {
    PatientData get(long patientId) throws JsonProcessingException, PatientNotFoundException;
    PatientData get(String family) throws PatientNotFoundException, PatientNotUniqueException;
}
