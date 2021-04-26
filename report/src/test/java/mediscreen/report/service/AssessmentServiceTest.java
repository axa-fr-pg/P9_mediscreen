package mediscreen.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientRiskDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static mediscreen.report.service.AssessmentService.RISK_BORDERLINE;
import static mediscreen.report.service.AssessmentService.RISK_EARLY_ONSET;
import static mediscreen.report.service.AssessmentService.RISK_IN_DANDER;
import static mediscreen.report.service.AssessmentService.RISK_NONE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AssessmentServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PatientService patientService;

    @MockBean
    NoteService noteService;

    @Autowired
    AssessmentService service;
    AssessmentServiceImpl serviceImpl;

    final private PatientData patientData = new PatientData(1, "2", "3",
            LocalDate.of(2004, 5, 6),
            "7", "8", "9");

    private final List<String> testRiskTriggerWordList;

    public AssessmentServiceTest() throws IOException {
        serviceImpl = new AssessmentServiceImpl();
        testRiskTriggerWordList = new ArrayList<>(serviceImpl.riskTriggerWordList);
    }

    private List<NoteData> noteDataListWithTriggers(int numberOfTriggers) {
        List<NoteData> noteDataList = new ArrayList<>();
        for (int i=0; i<numberOfTriggers; i++) {
            String id = "id-before-" + i;
            String e = randomAlphabetic(nextInt(3, 15)) + " "  + randomAlphabetic(nextInt(3, 15));
            noteDataList.add(new NoteData(id, e));
        }
        for (int i=0; i<numberOfTriggers; i++) {
            String id = "id-" + i;
            String e = randomAlphabetic(nextInt(3, 15)) + " "  +
                    testRiskTriggerWordList.get(nextInt(0,testRiskTriggerWordList.size()-1)) + " " +
                    randomAlphabetic(nextInt(3, 15));
            noteDataList.add(new NoteData(id, e));
        }
        for (int i=0; i<numberOfTriggers; i++) {
            String id = "id-after-" + i;
            String e = randomAlphabetic(nextInt(3, 15)) + " "  + randomAlphabetic(nextInt(3, 15));
            noteDataList.add(new NoteData(id, e));
        }
        return noteDataList;
    }

    @BeforeEach
    public void init() {
        reset(noteService);
        reset(patientService);
    }

    @Test
    public void test_getByPatient_ok() throws IOException, DoctorUnavailableException {
        // GIVEN
        int age = Period.between(patientData.dob, LocalDate.now()).getYears();
        when(noteService.getList(patientData.id)).thenReturn(Collections.emptyList());
        // WHEN
        PatientAssessmentDTO patientAssessmentDTO = service.get(patientData);
        // THEN
        assertTrue(patientAssessmentDTO.assessment.contains(Integer.toString(age)));
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.family));
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.given));
        assertTrue(patientAssessmentDTO.assessment.contains("None"));
    }

    @Test
    public void test_assessment_hasCorrectStringContent() {
        // GIVEN
        int age = Period.between(patientData.dob, LocalDate.now()).getYears();
        String expectedAssessment = "Patient: " + patientData.family +
                " " + patientData.given + " (age " + age + ") diabetes assessment is: None";
        // WHEN
        PatientAssessmentDTO patientAssessmentDTO = serviceImpl.assessment(patientData, Collections.emptyList());
        // THEN
        assertEquals(expectedAssessment, patientAssessmentDTO.assessment);
    }

    @Test
    public void test_getByPatientId_ok() throws IOException, DoctorUnavailableException, PatientNotFoundException {
        // GIVEN
        when(patientService.get(patientData.id)).thenReturn(patientData);
        when(noteService.getList(patientData.id)).thenReturn(Collections.emptyList());
        // WHEN
        PatientAssessmentDTO patientAssessmentDTO = service.get(patientData.id);
        // THEN
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.family));
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.given));
    }

    @Test
    public void test_getByFamily_ok() throws IOException, DoctorUnavailableException,
            PatientNotFoundException, PatientNotUniqueException {
        // GIVEN
        when(patientService.get(patientData.family)).thenReturn(patientData);
        when(noteService.getList(patientData.id)).thenReturn(Collections.emptyList());
        // WHEN
        PatientAssessmentDTO patientAssessmentDTO = service.get(patientData.family);
        // THEN
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.family));
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.given));
    }

    @Test
    public void test_getAll_ok() throws IOException, DoctorUnavailableException {
        // GIVEN
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<PatientData> patientDataList = Arrays.asList(patientData, patientData, patientData);
        Page<PatientData> pagePatientData = new PageImpl<>(patientDataList, pageRequest, patientDataList.size());
        when(patientService.getPage(pageRequest, null, null)).thenReturn(pagePatientData);
        patientDataList.forEach(patientData -> {
            try {
                when(patientService.get(patientData.id)).thenReturn(new PatientData());
                when(noteService.getList(patientData.id)).thenReturn(Collections.emptyList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // WHEN
        Page<PatientRiskDTO> page = service.get(pageRequest);
        // THEN
        assertNotNull(page);
        assertEquals(pagePatientData.getTotalElements(), page.getTotalElements());
    }

    @Test
    public void test_getAll_doctorUnavailable() throws IOException, DoctorUnavailableException {
        // GIVEN
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<PatientData> patientDataList = Arrays.asList(patientData, patientData, patientData);
        Page<PatientData> pagePatientData = new PageImpl<>(patientDataList, pageRequest, patientDataList.size());
        when(patientService.getPage(pageRequest, null, null)).thenReturn(pagePatientData);
        when(noteService.getList(patientData.id)).thenThrow(DoctorUnavailableException.class);
        // WHEN & THEN
        assertThrows(DoctorUnavailableException.class, () -> service.get(pageRequest));
    }

    @Test
    public void test_risk_none_without_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(0);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_NONE, risk);
    }

    @Test
    public void test_risk_none_with_two_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(2);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_NONE, risk);
    }

    @Test
    public void test_risk_none_with_three_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(3);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        patientData.sex = "F";
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_NONE, risk);
    }

    @Test
    public void test_risk_borderline_two_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(2);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-32, 5, 6);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_BORDERLINE, risk);
    }

    @Test
    public void test_risk_borderline_five_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(5);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-32, 5, 6);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_BORDERLINE, risk);
    }

    @Test
    public void test_risk_inDanger_three_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(3);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        patientData.sex = "M";
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_IN_DANDER, risk);
    }

    @Test
    public void test_risk_inDanger_four_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(4);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        patientData.sex = "F";
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_IN_DANDER, risk);
    }

    @Test
    public void test_risk_inDanger_six_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(6);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-32, 5, 6);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_IN_DANDER, risk);
    }

    @Test
    public void test_risk_earlyOnset_five_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(5);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        patientData.sex = "M";
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_EARLY_ONSET, risk);
    }

    @Test
    public void test_risk_earlyOnset_seven_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(7);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        patientData.sex = "F";
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_EARLY_ONSET, risk);
    }

    @Test
    public void test_risk_earlyOnset_eight_triggers() {
        // GIVEN
        List<NoteData> noteDataList = noteDataListWithTriggers(5);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-28, 5, 6);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_EARLY_ONSET, risk);
    }
    @Test
    public void test_risk_borderline_two_triggers_case_doesnt_matter() {
        // GIVEN
        for (int i=0; i<testRiskTriggerWordList.size(); i++) {
            String word = testRiskTriggerWordList.get(0);
            testRiskTriggerWordList.remove(0);
            String newWord = (i%2==0) ? word.toLowerCase() : word.toUpperCase();
            testRiskTriggerWordList.add(newWord);
        }
        List<NoteData> noteDataList = noteDataListWithTriggers(2);
        patientData.dob = LocalDate.of(LocalDate.now().getYear()-32, 5, 6);
        // WHEN
        String risk = serviceImpl.risk(patientData, noteDataList);
        // THEN
        assertEquals(RISK_BORDERLINE, risk);
    }
}
