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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static mediscreen.patient.controller.ExceptionManager.EXCEPTION_MANAGER_CREATE_EXISTING_PATIENT;
import static mediscreen.patient.controller.ExceptionManager.EXCEPTION_MANAGER_PATIENT_NOT_FOUND;
import static mediscreen.patient.model.PatientDTO.ADDRESS_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientDTO.DOB_PAST_ERROR;
import static mediscreen.patient.model.PatientDTO.FAMILY_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientDTO.SEX_PATTERN_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private PatientEntity mockEntityFind(long id, boolean exists)  {
        PatientEntity patient = PatientEntity.random();
        patient.id = id;
        Optional<PatientEntity> optional = exists ? Optional.of(patient) : Optional.empty();
        when(repository.findById(id)).thenReturn(optional);
        return patient;
    }

    private PatientEntity mockEntitySave(Long patientId)  {
        PatientEntity patient = PatientEntity.random();
        patient.id = patientId;
        when(repository.save(any(PatientEntity.class))).thenReturn(patient);
        return patient;
    }

    private PatientEntity mockEntityCreate()  {
        PatientEntity patient = PatientEntity.random();
        patient.id = new Random().nextLong();
        when(repository.save(any(PatientEntity.class))).thenReturn(patient);
        return patient;
    }

    private MockHttpServletRequestBuilder buildPutRequest(PatientEntity patient) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .put(ENTITY_URL + "/" + patient.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PatientDTO(patient)))
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder buildPostRequest(PatientEntity patient) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(ENTITY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PatientDTO(patient)))
                .accept(MediaType.APPLICATION_JSON);
    }

    private void assertEntityEqual(PatientEntity expected, PatientEntity received) throws JsonProcessingException {
        assertEquals(expected.id, received.id);
        assertEquals(expected.family, received.family);
        assertEquals(expected.given, received.given);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        String expectedDob = objectMapper.writeValueAsString(expected.dob);
        String receivedDob = objectMapper.writeValueAsString(received.dob);
        assertEquals(expectedDob, receivedDob);
        assertEquals(expected.sex, received.sex);
        assertEquals(expected.address, received.address);
        assertEquals(expected.phone, received.phone);
    }

    @Test
    public void givenExistingPatient_whenGet_thenReturnsCorrectPatient() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntityFind(93, true);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL + "/" + patient.id)).andReturn().getResponse();
        PatientDTO result = objectMapper.readValue(response.getContentAsString(), PatientDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEntityEqual(patient, new PatientEntity(result));
    }

    @Test
    public void givenNoPatient_whenGet_thenReturnsNotFound() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntityFind(105, false);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get(ENTITY_URL + "/" + patient.id)).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, response.getContentAsString());
    }

    @Test
    public void givenExistingPatient_whenPut_thenReturnsCorrectPatient() throws Exception {
        // GIVEN
        PatientEntity patientBefore = mockEntityFind(116, true);
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        MockHttpServletRequestBuilder builder = buildPutRequest(patientAfter);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        PatientDTO result = objectMapper.readValue(response.getContentAsString(), PatientDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEntityEqual(patientAfter, new PatientEntity(result));
    }

    @Test
    public void givenExistingPatient_whenPutWithSexS_thenReturnsSexPatternError() throws Exception {
        // GIVEN
        PatientEntity patientBefore = mockEntityFind(130, true);
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        patientAfter.sex = "S";
        MockHttpServletRequestBuilder builder = buildPutRequest(patientAfter);
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
        PatientEntity patientBefore = mockEntityFind(146, true);
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        patientAfter.dob = new Date((new Date()).getTime()+24*60*60*1000L);
        MockHttpServletRequestBuilder builder = buildPutRequest(patientAfter);
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
        PatientEntity patientBefore = mockEntityFind(161, true);
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        patientAfter.family = "";
        MockHttpServletRequestBuilder builder = buildPutRequest(patientAfter);
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
        PatientEntity patientBefore = mockEntityFind(201, false);
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        MockHttpServletRequestBuilder builder = buildPutRequest(patientAfter);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, response.getContentAsString());
    }

    @Test
    public void givenNewPatient_whenPostWithAddressBlank_thenReturnsAddressNotBlankError() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntityCreate();
        PatientEntity request = new PatientEntity(new PatientDTO(patient));
        request.address = "";
        MockHttpServletRequestBuilder builder = buildPostRequest(request);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(ADDRESS_NOT_BLANK_ERROR, errors.get(0));
    }

    @Test
    public void givenPatientSameFamilyAndDob_whenPost_thenReturnsCreateExistingPatientError() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntityFind(230, true);
        when(repository.findByFamilyAndDob(eq(patient.family), any(Date.class))).thenReturn(Collections.singletonList(patient));
        PatientEntity duplicate = new PatientEntity(new PatientDTO(patient));
        duplicate.id = 0;
        duplicate.family = patient.family;
        duplicate.dob = patient.dob;
        MockHttpServletRequestBuilder builder = buildPostRequest(duplicate);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_CREATE_EXISTING_PATIENT, response.getContentAsString());
    }

    @Test
    public void givenNewPatient_whenPost_thenReturnsCorrectPatient() throws Exception {
        // GIVEN
        PatientEntity patient = mockEntityCreate();
        PatientEntity request = new PatientEntity(new PatientDTO(patient));
        request.id = 0;
        MockHttpServletRequestBuilder builder = buildPostRequest(request);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        PatientDTO result = objectMapper.readValue(response.getContentAsString(), PatientDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEntityEqual(patient, new PatientEntity(result));
    }
}
