package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientData;

import java.io.IOException;
import java.util.List;

public interface NoteService {
    List<NoteData> getList(long patientId) throws DoctorUnavailableException, IOException;
}
