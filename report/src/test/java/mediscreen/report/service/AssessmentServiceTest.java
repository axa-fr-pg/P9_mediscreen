package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import mediscreen.report.client.NoteClient;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientNotesData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AssessmentServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    NoteClient client;

    @Autowired
    AssessmentService service;

    private Response buildDoctorResponse(HttpStatus httpStatus, long patientId, List<NoteData> noteDataList) throws JsonProcessingException {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "",
                Collections.emptyMap(),
                null,
                null);
        PatientNotesData patientNotesData = new PatientNotesData(patientId, noteDataList);
        return Response.builder()
                .request(request)
                .status(httpStatus.value())
                .body(objectMapper.writeValueAsString(patientNotesData), StandardCharsets.UTF_8)
                .build();
    }

    @Test
    public void test_getByPatient_ok() throws JsonProcessingException, DoctorUnavailableException {
        // GIVEN
        int age = 27;
        int birthYear = LocalDate.now().getYear() - age;
        PatientData patientData = new PatientData(1, "2", "3",
                LocalDate.of(birthYear,5,6), "7", "8", "9");
        Response response = buildDoctorResponse(HttpStatus.OK, patientData.id, Collections.emptyList());
        when(client.getPatientNotes(patientData.id)).thenReturn(response);
        // WHEN
        PatientAssessmentDTO patientAssessmentDTO = service.getByPatient(patientData);
        // THEN
        assertTrue(patientAssessmentDTO.assessment.contains("Patient:"));
        assertTrue(patientAssessmentDTO.assessment.contains("diabetes assessment is:"));
        assertTrue(patientAssessmentDTO.assessment.contains("None"));
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.family));
        assertTrue(patientAssessmentDTO.assessment.contains(patientData.given));
        assertTrue(patientAssessmentDTO.assessment.contains("(age " + age));
    }

    @Test
    public void test_getByPatient_serverError() throws JsonProcessingException {
        // GIVEN
        PatientData patientData = new PatientData();
        Response response = buildDoctorResponse(HttpStatus.INTERNAL_SERVER_ERROR, patientData.id, null);
        when(client.getPatientNotes(patientData.id)).thenReturn(response);
        String message = "test failed";
        // WHEN
        try {
            service.getByPatient(patientData);
        } catch (DoctorUnavailableException e) {
            message = e.getMessage();
        }
        // THEN
        assertTrue(message.contains("Could not check notes for patient with id"));
        assertTrue(message.contains("received return code 500 from API"));
    }
}
