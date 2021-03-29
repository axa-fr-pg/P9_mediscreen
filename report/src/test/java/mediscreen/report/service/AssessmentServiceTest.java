package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

}
