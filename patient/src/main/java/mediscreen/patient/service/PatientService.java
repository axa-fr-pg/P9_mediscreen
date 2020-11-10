package mediscreen.patient.service;

import mediscreen.patient.model.PatientDTO;

import java.util.List;

public interface PatientService {
    List<PatientDTO> getList();
    PatientDTO get(long patientId) throws PatientNotFoundException;
}
