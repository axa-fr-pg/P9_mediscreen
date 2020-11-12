package mediscreen.patient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.patient.model.PatientEntity;
import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static mediscreen.patient.controller.ExceptionManager.EXCEPTION_MANAGER_PATIENT_NOT_FOUND;
import static mediscreen.patient.model.PatientDTO.DOB_PAST_ERROR;
import static mediscreen.patient.model.PatientDTO.FAMILY_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientDTO.SEX_PATTERN_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PatientRepository repository;

    private final static String ENTITY_URL = "/patients";

    @BeforeEach
    public void init() {
        reset(repository);
    }

    private PatientEntity mockEntity(long id, boolean exists)  {
        PatientEntity patient = PatientEntity.random();
        patient.id = id;
        PatientDTO display = new PatientDTO(patient);
        Optional<PatientEntity> optional = exists ? Optional.of(patient) : Optional.empty();
        when(repository.findById(id)).thenReturn(optional);
        when(repository.save(patient)).thenReturn(patient);
        return patient;
    }

    private MockHttpServletRequestBuilder buildRequest(PatientEntity patient) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .put(ENTITY_URL + "/" + patient.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PatientDTO(patient)))
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    public void givenExistingPatient_whenGet_thenReturnsCorrectPatient() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(62, true);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL + "/" + patient.id)).andReturn().getResponse();
        PatientDTO result = objectMapper.readValue(response.getContentAsString(), PatientDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(patient.id, result.id);
    }

    @Test
    public void givenNoPatient_whenGet_thenReturnsNotFound() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(74, false);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get(ENTITY_URL + "/" + patient.id)).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, response.getContentAsString());
    }

    @Test
    public void givenExistingPatient_whenPut_thenReturnsCorrectPatient() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(85, true);
        MockHttpServletRequestBuilder builder = buildRequest(patient);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        PatientDTO result = objectMapper.readValue(response.getContentAsString(), PatientDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(patient.id, result.id);
    }

    @Test
    public void givenExistingPatient_whenPutWithSexS_thenReturnsSexPatternError() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(85, true);
        patient.sex = "S";
        MockHttpServletRequestBuilder builder = buildRequest(patient);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(SEX_PATTERN_ERROR, errors.get(0));
    }

    @Test
    public void givenExistingPatient_whenPutWithDobFuture_thenReturnsDobPastError() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(85, true);
        patient.dob = new Date((new Date()).getTime()+24*60*60*1000L);
        MockHttpServletRequestBuilder builder = buildRequest(patient);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(DOB_PAST_ERROR, errors.get(0));
    }

    @Test
    public void givenExistingPatient_whenPutWithFamilyBlank_thenReturnsFamilyNotBlankError() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(85, true);
        patient.family = "";
        MockHttpServletRequestBuilder builder = buildRequest(patient);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(FAMILY_NOT_BLANK_ERROR, errors.get(0));
    }

    @Test
    public void givenNoPatient_whenPut_thenReturnsNotFound() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntity(92, false);
        MockHttpServletRequestBuilder builder = buildRequest(patient);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, response.getContentAsString());
    }
}
