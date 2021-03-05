package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientData;

public interface PatientService {
    PatientData getByPatientId(long patientId) throws JsonProcessingException, PatientNotFoundException;
    PatientData getByFamily(String family) throws PatientNotFoundException, PatientNotUniqueException;
}
