package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import mediscreen.report.client.NoteClient;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientNotesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final static String RISK_NONE = "None";

    @Autowired
    NoteClient client;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public PatientAssessmentDTO getByPatient(PatientData patientData) throws DoctorUnavailableException, JsonProcessingException {
        Response response = client.getPatientNotes(patientData.id);
        if (response.status() == HttpStatus.OK.value()) {
            PatientNotesData patientNotesData = objectMapper.readValue(response.body().toString(), PatientNotesData.class);
            return assessment(patientData, patientNotesData.noteDTOList);
        }
        throw new DoctorUnavailableException(
                "Could not check notes for patient with id " + patientData.id +
                        " : received return code " + response.status() + " from API."
        );
    }

    // This method is public for testing purpose but no exposure in the interface is required
    public PatientAssessmentDTO assessment(PatientData patientData, List<NoteData> noteDTOList) {
        // Sample = Patient: Test TestNone (age 52) diabetes assessment is: None
        int age = LocalDate.now().getYear() - patientData.dob.getYear();
        return(new PatientAssessmentDTO(
                "Patient: " +
                patientData.family + " " +
                        patientData.given +
                        " (age " + age +
                        ") diabetes assessment is: " +
                        risk(patientData, noteDTOList)
        ));
    }

    // This method is public for testing purpose but no exposure in the interface is required
    private String risk(PatientData patientData, List<NoteData> noteDTOList) {
        return RISK_NONE;
    }
}
