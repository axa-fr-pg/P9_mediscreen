package mediscreen.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import mediscreen.report.client.DoctorClient;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientNotesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    DoctorClient doctorClient;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<NoteData> getList(long patientId) throws DoctorUnavailableException, IOException {
        Response response = doctorClient.getPatientNotes(patientId);
        if (response.status() == HttpStatus.OK.value()) {
            PatientNotesData patientNotesData = objectMapper.readValue(response.body().asInputStream(), PatientNotesData.class);
            return patientNotesData.noteDTOList;
        }
        throw new DoctorUnavailableException(
                "Could not check notes for patient with id " + patientId +
                        " : received return code " + response.status() + " from API."
        );
    }
}
