package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    final private PatientData patientData = new PatientData(1, "2", "3",
            LocalDate.of(2004, 5, 6),
            "7", "8", "9");

    @BeforeEach
    public void init() {
        reset(noteService);
        reset(patientService);
    }

    @Test
    public void test_getByPatient_ok() throws JsonProcessingException, DoctorUnavailableException {
        // GIVEN
        int age = 27;
        int birthYear = LocalDate.now().getYear() - age;
        patientData.dob = patientData.dob.withYear(birthYear);
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
        int age = 43;
        int birthYear = LocalDate.now().getYear() - age;
        patientData.dob = patientData.dob.withYear(birthYear);
        String expectedAssessment = "Patient: " + patientData.family +
                " " + patientData.given + " (age " + age + ") diabetes assessment is: None";
        // WHEN
        PatientAssessmentDTO patientAssessmentDTO = AssessmentServiceImpl
                .assessment(patientData, Collections.emptyList());
        // THEN
        assertEquals(expectedAssessment, patientAssessmentDTO.assessment);
    }

    @Test
    public void test_getByPatientId_ok() throws JsonProcessingException, DoctorUnavailableException, PatientNotFoundException {
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
    public void test_getByFamily_ok() throws JsonProcessingException, DoctorUnavailableException,
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
    public void test_getAll_ok() throws Throwable {
        // GIVEN
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<Long> patientIdList = Arrays.asList(111L, 112L, 113L);
        Page<Long> pagePatientId = new PageImpl<>(patientIdList, pageRequest, patientIdList.size());
        when(patientService.getAllId(pageRequest)).thenReturn(pagePatientId);
        patientIdList.forEach(patientId -> {
            try {
                when(patientService.get(patientId)).thenReturn(new PatientData());
                when(noteService.getList(patientId)).thenReturn(Collections.emptyList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // WHEN
        Page<PatientRiskDTO> page = service.get(pageRequest);
        // THEN
        assertNotNull(page);
        assertEquals(pagePatientId.getTotalElements(), page.getTotalElements());
    }

    @Test
    public void test_getAll_patientNotFound() throws Throwable {
        // GIVEN
        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<Long> pagePatientId = new PageImpl<>(Arrays.asList(111L, 112L, 113L));
        when(patientService.getAllId(pageRequest)).thenReturn(pagePatientId);
        pagePatientId.forEach(patientId -> {
            try {
                when(patientService.get(patientId)).thenThrow(new PatientNotFoundException(""));
                when(noteService.getList(patientId)).thenReturn(Collections.emptyList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // WHEN & THEN
        assertThrows(PatientNotFoundException.class, () -> service.get(pageRequest));
    }
}
