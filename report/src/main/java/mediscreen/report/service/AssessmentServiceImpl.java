package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import mediscreen.report.client.NoteClient;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientNotesData;
import mediscreen.report.model.PatientRiskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private List<PatientRiskDTO> convertPatientIdListToPatientRiskDtoList(List<Long> patientIdList) {
        List<PatientRiskDTO> patientRiskDTOList = new ArrayList<>();
        patientIdList.forEach( patientId -> {
            PatientData patientData;
            try {
                patientData = patientService.get(patientId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<NoteData> noteDataList;
            try {
                noteDataList = noteService.getList(patientId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String risk = risk(patientData, noteDataList);
            patientRiskDTOList.add(new PatientRiskDTO(patientId, patientData.family, risk));
        });
        return patientRiskDTOList;
    }

    @Override
    public Page<PatientRiskDTO> get(Pageable pageRequest) throws Throwable {
        Page<Long> pagePatientId = patientService.getAllId(pageRequest);
        Page<PatientRiskDTO> patientRiskDTOPage;
        try {
            patientRiskDTOPage = new PageImpl<>(
                    convertPatientIdListToPatientRiskDtoList(pagePatientId.toList()),
                    pageRequest, pagePatientId.getTotalElements());
        } catch (RuntimeException e) {
            throw e.getCause();
        }
        return patientRiskDTOPage;
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
