package mediscreen.report.controller;

import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientRiskDTO;
import mediscreen.report.service.AssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ReportControllerTest {

    @MockBean
    AssessmentService service;

    @Autowired
    ReportController controller;

    @BeforeEach
    public void init() {
        reset(service);
    }

    @Test
    public void test_getByPatientId_ok() throws Throwable {
        // GIVEN
        long patientId = 29;
        String assessment = "test-assessment";
        when(service.get(patientId)).thenReturn(new PatientAssessmentDTO(assessment));
        // WHEN
        ResponseEntity<Object>  result = controller.get(patientId, null, null);
        // THEN
        assertNotNull(result);
        PatientAssessmentDTO patientAssessmentDTO = (PatientAssessmentDTO) result.getBody();
        assertNotNull(patientAssessmentDTO);
        assertEquals(assessment, patientAssessmentDTO.assessment);
    }

    @Test
    public void test_getByFamily_ok() throws Throwable {
        // GIVEN
        String family = "test-family-name";
        String assessment = "test-assessment";
        when(service.get(family)).thenReturn(new PatientAssessmentDTO(assessment));
        // WHEN
        ResponseEntity<Object>  result = controller.get(null, family, null);
        // THEN
        assertNotNull(result);
        PatientAssessmentDTO patientAssessmentDTO = (PatientAssessmentDTO) result.getBody();
        assertNotNull(patientAssessmentDTO);
        assertEquals(assessment, patientAssessmentDTO.assessment);
    }

    @Test
    public void test_getByPatientIdAndByFamily_conflict() throws Throwable {
        // GIVEN
        long patientId = 73;
        String family = "test-family-name";
        // WHEN
        String message = "test failed";
        try {
            controller.get(patientId, family, null);
        } catch (RequestParamConflictException e) {
            message = e.getMessage();
        }
        // THEN
        assertTrue(message.contains("You cannot assess at the same time by patient id and by family"));
    }

    @Test
    public void test_getAll_ok() throws Throwable {
        // GIVEN
        PatientRiskDTO patientRiskDTO1 = new PatientRiskDTO(1, "family-1", "risk-1");
        PatientRiskDTO patientRiskDTO2 = new PatientRiskDTO(2, "family-2", "risk-2");
        PatientRiskDTO patientRiskDTO3 = new PatientRiskDTO(3, "family-3", "risk-3");
        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<PatientRiskDTO> patientRiskDTOPage = new PageImpl<>(
                Arrays.asList(patientRiskDTO1, patientRiskDTO2, patientRiskDTO3),
                pageRequest, 0) ;
        when(service.get(pageRequest)).thenReturn(patientRiskDTOPage);
        // WHEN
        ResponseEntity<Object>  result = controller.get(null, null, pageRequest);
        // THEN
        assertNotNull(result);
        Page<PatientRiskDTO> page = (Page<PatientRiskDTO>) result.getBody();
        assertNotNull(page);
        assertEquals(patientRiskDTOPage.getTotalElements(), page.getTotalElements());
    }
}
