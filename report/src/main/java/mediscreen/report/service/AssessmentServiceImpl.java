package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientRiskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
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
    public PatientAssessmentDTO get(@NotNull PatientData patientData)
            throws DoctorUnavailableException, IOException {
        return assessment(patientData, noteService.getList(patientData.id));
    }

    @Override
    public PatientAssessmentDTO get(long patientId) throws
            IOException, PatientNotFoundException, DoctorUnavailableException {
        PatientData patientData = patientService.get(patientId);
        return get(patientData);
    }

    @Override
    public PatientAssessmentDTO get(String family) throws PatientNotUniqueException,
            PatientNotFoundException, IOException, DoctorUnavailableException {
        PatientData patientData = patientService.get(family);
        return get(patientData);
    }

    private List<PatientRiskDTO> convertPatientDataListToPatientRiskDTOList(List<PatientData> patientDataList)
            throws IOException, DoctorUnavailableException {
        List<PatientRiskDTO> patientRiskDTOList = new ArrayList<>();
        for (PatientData patientData : patientDataList) {
            List<NoteData> noteDataList = noteService.getList(patientData.id);
            String risk = risk(patientData, noteDataList);
            patientRiskDTOList.add(new PatientRiskDTO(patientData.id, patientData.family, risk));
        }
        return patientRiskDTOList;
    }

    @Override
    public Page<PatientRiskDTO> get(Pageable pageRequest) throws IOException, DoctorUnavailableException {
        Page<PatientData> patientDataPage = patientService.getPage(pageRequest);
        Page<PatientRiskDTO> patientRiskDTOPage;
        patientRiskDTOPage = new PageImpl<>(
                convertPatientDataListToPatientRiskDTOList(patientDataPage.toList()),
                pageRequest, patientDataPage.getTotalElements());
        return patientRiskDTOPage;
    }

    // This method is public for testing purpose but no exposure in the interface is required
    public PatientAssessmentDTO assessment(PatientData patientData, List<NoteData> noteDTOList) {
        int age = Period.between(patientData.dob, LocalDate.now()).getYears();
        return (new PatientAssessmentDTO( "Patient: " + patientData.family + " " + patientData.given +
                        " (age " + age + ") diabetes assessment is: " + risk(patientData, noteDTOList)));
    }

    // This method is public for testing purpose but no exposure in the interface is required
    private String risk(PatientData patientData, List<NoteData> noteDTOList) {
        return RISK_NONE;
    }
}
