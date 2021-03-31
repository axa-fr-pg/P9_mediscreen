package mediscreen.report.service;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    PatientService patientService;

    @Autowired
    NoteService noteService;

    final static protected List<String> riskTriggerWordList = Arrays.asList(
            "Hémoglobine A1C",
            "Microalbumine",
            "Taille",
            "Poids",
            "Fumeur",
            "Anormal",
            "Cholestérol",
            "Vertige",
            "Rechute",
            "Réaction",
            "Anticorps"
    );

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
        return get(pageRequest, null, null);
    }

    @Override
    public Page<PatientRiskDTO> get(Pageable pageRequest, String filterId, String filterFamily)
            throws IOException, DoctorUnavailableException {
        Page<PatientData> patientDataPage = patientService.getPage(pageRequest, filterId, filterFamily);
        List<PatientRiskDTO> patientRiskDTOList = convertPatientDataListToPatientRiskDTOList(patientDataPage.toList());
        return new PageImpl<>(patientRiskDTOList, pageRequest, patientDataPage.getTotalElements());
    }

    private int age(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    // This method is public for testing purposes but no exposure in the interface is required
    public PatientAssessmentDTO assessment(PatientData patientData, List<NoteData> noteDataList) {
        return (new PatientAssessmentDTO( "Patient: " + patientData.family + " " + patientData.given +
                        " (age " + age(patientData.dob) + ") diabetes assessment is: " + risk(patientData, noteDataList)));
    }

    private long numberOfNotesWithTrigger(List<NoteData> noteDataList, String word) {
        return noteDataList.stream().filter(noteData -> noteData.e.contains(word)).count();
    }

    private long totalRiskTriggers(List<NoteData> noteDataList) {
        Map<String, Long> numberOfNotesPerTrigger = riskTriggerWordList.stream().collect(
                Collectors.toMap(word->word, word->numberOfNotesWithTrigger(noteDataList, word)));
        return numberOfNotesPerTrigger.values().stream().reduce(0L,Long::sum);
    }

    private String riskFemaleUnderThirtyYears(long numberOfTriggers) {
        if (numberOfTriggers <4) {
            return RISK_NONE;
        }
        if (numberOfTriggers <7) {
            return RISK_IN_DANDER;
        }
        return RISK_EARLY_ONSET;
    }

    private String riskMaleUnderThirtyYears(long numberOfTriggers) {
        if (numberOfTriggers <3) {
            return RISK_NONE;
        }
        if (numberOfTriggers <5) {
            return RISK_IN_DANDER;
        }
        return RISK_EARLY_ONSET;
    }

    private String riskUnderThirtyYears(String sex, long numberOfTriggers) {
        if (sex.equals("F")) {
            return riskFemaleUnderThirtyYears(numberOfTriggers);
        }
        return riskMaleUnderThirtyYears(numberOfTriggers);
    }

    private String riskAboveThirtyYears(long numberOfTriggers) {
        if (numberOfTriggers <2) {
            return RISK_NONE;
        }
        if (numberOfTriggers <6) {
            return RISK_BORDERLINE;
        }
        if (numberOfTriggers <8) {
            return RISK_IN_DANDER;
        }
        return RISK_EARLY_ONSET;
    }

    // This method is public for testing purposes but no exposure in the interface is required
    public String risk(PatientData patientData, List<NoteData> noteDataList) {
        long numberOfTriggers = totalRiskTriggers(noteDataList);
        if (age(patientData.dob) > 30) {
            return riskAboveThirtyYears(numberOfTriggers);
        };
        return riskUnderThirtyYears(patientData.sex, numberOfTriggers);
    }
}
