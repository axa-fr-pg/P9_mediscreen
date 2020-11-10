package mediscreen.patient.service;

import mediscreen.patient.model.PatientDisplay;
import mediscreen.patient.model.PatientId;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

public interface PatientService {
    public List<PatientDisplay> getList();
    public PatientDisplay get(long patientId) throws PatientNotFoundException;
}
