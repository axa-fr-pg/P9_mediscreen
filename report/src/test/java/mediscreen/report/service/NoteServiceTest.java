package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import mediscreen.report.client.DoctorClient;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientNotesData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NoteServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NoteService service;

    @MockBean
    DoctorClient doctorClient;

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
    public void test_getByPatientId_ok() throws JsonProcessingException, DoctorUnavailableException {
        // GIVEN
        long patientId = 53;
        NoteData noteData1 = new NoteData("id-1", "content 1");
        NoteData noteData2 = new NoteData("id-2", "content 2");
        NoteData noteData3 = new NoteData("id-3", "content 3");
        List<NoteData> noteDataList = Arrays.asList(noteData1, noteData2, noteData3);
        Response response = buildDoctorResponse(HttpStatus.OK, patientId, noteDataList);
        when(doctorClient.getPatientNotes(patientId)).thenReturn(response);
        // WHEN
        List<NoteData> result = service.getList(patientId);
        // THEN
        assertEquals(noteDataList.size(), result.size());
    }

    @Test
    public void test_getByPatient_serverError() throws JsonProcessingException {
        // GIVEN
        long patientId = 62;
        Response response = buildDoctorResponse(HttpStatus.INTERNAL_SERVER_ERROR, patientId, null);
        when(doctorClient.getPatientNotes(patientId)).thenReturn(response);
        String message = "test failed";
        // WHEN
        try {
            service.getList(patientId);
        } catch (DoctorUnavailableException e) {
            message = e.getMessage();
        }
        // THEN
        assertTrue(message.contains("Could not check notes for patient with id"));
        assertTrue(message.contains("received return code 500 from API"));
    }
}
