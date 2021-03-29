package mediscreen.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import mediscreen.report.client.DoctorClient;
import mediscreen.report.client.PatientClient;
import mediscreen.report.model.NoteData;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientNotesData;
import mediscreen.report.model.PatientRiskDTO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PatientClient patientClient;

    @MockBean
    DoctorClient doctorClient;

    private Request request;
    PatientData patientData;

    @Autowired
    private MockMvc mockMvc;

    private final static String ENTITY_URL = "/reports/patients";

    @BeforeEach
    public void init() {
        reset(patientClient);
        request = Request.create(Request.HttpMethod.GET,"",
                Collections.emptyMap(),null,null);
        patientData = new PatientData(1, "2", "3",
                LocalDate.of(2004,5,6), "7", "8", "9");
    }

    private Response buildDoctorResponse(HttpStatus httpStatus, long patientId, List<NoteData> noteDataList) throws JsonProcessingException {
        PatientNotesData patientNotesData = new PatientNotesData(patientId, noteDataList);
        return Response.builder()
                .request(request)
                .status(httpStatus.value())
                .body(objectMapper.writeValueAsString(patientNotesData), StandardCharsets.UTF_8)
                .build();
    }

    private Response buildPatientResponse(HttpStatus httpStatus) throws JsonProcessingException {
        return Response.builder()
                .request(request)
                .status(httpStatus.value())
                .body(objectMapper.writeValueAsString(patientData), StandardCharsets.UTF_8)
                .build();
    }

    private Page<PatientData> buildPatientPage(int numberOfElements) throws JsonProcessingException {
        PageRequest pageRequest = PageRequest.of(0, 2);
        ArrayList<PatientData> patientDataList = new ArrayList<>();
        Stream.generate(PatientData::new).limit(numberOfElements).forEach(patientDataList::add);
        return new PageImpl<>(patientDataList, pageRequest, 0) ;
    }

    @Test
    public void test_getAll_ok() throws Exception {
        // GIVEN
        int numberOfElements = 9;
        Page<PatientData> patientDataPage = buildPatientPage(numberOfElements);
        when(patientClient.get(0L)).thenReturn(buildPatientResponse(HttpStatus.OK));
        when(patientClient.getPage(any(Pageable.class), eq(null), eq(null), eq(null))).thenReturn(patientDataPage);
        when(doctorClient.getPatientNotes(anyLong()))
                .thenReturn(buildDoctorResponse(HttpStatus.OK, 0, Collections.emptyList()));
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get(ENTITY_URL)).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(numberOfElements, StringUtils.countMatches(response.getContentAsString(),"family"));
    }
}
