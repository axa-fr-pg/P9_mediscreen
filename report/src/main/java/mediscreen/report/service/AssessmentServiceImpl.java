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

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final static String RISK_NONE = "None";

    @Autowired
    PatientService patientService;

    @Autowired
    NoteService noteService;

    @Override
    public PatientAssessmentDTO get(@NotNull PatientData patientData) throws DoctorUnavailableException, JsonProcessingException {
        return assessment(patientData, noteService.getList(patientData.id));
    }

    @Override
    public PatientAssessmentDTO get(long patientId) throws
            JsonProcessingException, PatientNotFoundException, DoctorUnavailableException {
        PatientData patientData = patientService.get(patientId);
        return get(patientData);
    }

    @Override
    public PatientAssessmentDTO get(String family) throws PatientNotUniqueException, PatientNotFoundException, JsonProcessingException, DoctorUnavailableException {
        PatientData patientData = patientService.get(family);
        return get(patientData);
    }

    // This method is public for testing purpose but no exposure in the interface is required
    public static PatientAssessmentDTO assessment(PatientData patientData, List<NoteData> noteDTOList) {
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
    private static String risk(PatientData patientData, List<NoteData> noteDTOList) {
        return RISK_NONE;
    }
}
