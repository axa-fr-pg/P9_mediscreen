package mediscreen.report.service;

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

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
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
    AssessmentServiceImpl serviceImpl = new AssessmentServiceImpl();

    final private PatientData patientData = new PatientData(1, "2", "3",
            LocalDate.of(2004, 5, 6),
            "7", "8", "9");

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
}
