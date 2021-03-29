package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import mediscreen.report.client.PatientClient;
import mediscreen.report.model.PatientData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PatientServiceTest {

    @Autowired
    PatientService service;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PatientClient patientClient;

    @BeforeEach
    public void init() {
        reset(patientClient);
    }

    private Response buildPatientResponse(HttpStatus httpStatus) throws JsonProcessingException {
        PatientData patientData = new PatientData(1, "2", "3",
                LocalDate.of(2004,5,6), "7", "8", "9");
        Request request = Request.create(
                Request.HttpMethod.GET,
                "",
                Collections.emptyMap(),
                null,
                null);
        return Response.builder()
                .request(request)
                .status(httpStatus.value())
                .body(objectMapper.writeValueAsString(patientData), StandardCharsets.UTF_8)
                .build();
    }

    @Test
    public void test_getByPatientId_ok() throws JsonProcessingException, PatientNotFoundException {
        // GIVEN
        long patientId = 123456;
        Response response = buildPatientResponse(HttpStatus.OK);
        when(patientClient.get(patientId)).thenReturn(response);
        // WHEN
        String result = objectMapper.writeValueAsString(service.get(patientId));
        // THEN
        assertEquals(response.body().toString(), result);
    }

    @Test
    public void test_getByPatientId_notFound() throws JsonProcessingException {
        // GIVEN
        long patientId = 123456;
        Response response = buildPatientResponse(HttpStatus.NOT_FOUND);
        when(patientClient.get(patientId)).thenReturn(response);
        String message = "test failed";
        // WHEN
        try {
            service.get(patientId);
        } catch (PatientNotFoundException e) {
            message = e.getMessage();
        }
        // THEN
        assertTrue(message.contains("Could not find patient with id"));
        assertTrue(message.contains("received return code 404 from API"));
    }

    @Test
    public void test_getByFamily_ok() throws PatientNotFoundException, PatientNotUniqueException {
        // GIVEN
        String family = "family-name";
        PatientData patientData = new PatientData();
        Page<PatientData> page = new PageImpl<>(Collections.singletonList(patientData));
        when(patientClient.getPage(
                any(Pageable.class),
                anyString(),
                eq(family),
                anyString()))
                .thenReturn(page);
        // WHEN
        PatientData result = service.get(family);
        // THEN
        assertSame(patientData, result);
    }

    @Test
    public void test_getByFamily_notFound() throws PatientNotUniqueException {
        // GIVEN
        String family = "family-name";
        Page<PatientData> page = new PageImpl<>(Collections.emptyList());
        when(patientClient.getPage(
                any(Pageable.class),
                anyString(),
                eq(family),
                anyString()))
                .thenReturn(page);
        String message = "test failed";
        // WHEN
        try {
            service.get(family);
        } catch (PatientNotFoundException e) {
            message = e.getMessage();
        }
        // THEN
        assertTrue(message.contains("Could not find patient with name"));
        assertTrue(message.contains("received empty page as response from API"));
    }

    @Test
    public void test_getByFamily_notUnique() throws PatientNotFoundException {
        // GIVEN
        String family = "family-name";
        PatientData patientData = new PatientData();
        Page<PatientData> page = new PageImpl<>(Arrays.asList(patientData, patientData));
        when(patientClient.getPage(
                any(Pageable.class),
                anyString(),
                eq(family),
                anyString()))
                .thenReturn(page);
        String message = "test failed";
        // WHEN
        try {
            service.get(family);
        } catch (PatientNotUniqueException e) {
            message = e.getMessage();
        }
        // THEN
        assertTrue(message.contains("Could not find patient with name"));
        assertTrue(message.contains("received 2 matches from API"));
    }

    @Test
    public void test_getAllId_ok() {
        // GIVEN
        PageRequest pageRequest = PageRequest.of(1, 2);
        PatientData patientData1 = new PatientData();
        PatientData patientData2 = new PatientData();
        PatientData patientData3 = new PatientData();
        List<PatientData> patientDataList = Arrays.asList(patientData1, patientData2, patientData3);
        Page<PatientData> patientDataPage = new PageImpl<>(patientDataList, pageRequest, patientDataList.size());
        when(patientClient.getPage(pageRequest, null, null, null)).thenReturn(patientDataPage);
        // WHEN
        Page<Long> patientIdList = service.getAllId(pageRequest);
        // THEN
        assertEquals(patientDataPage.getTotalElements(), patientIdList.getTotalElements());
    }
}
