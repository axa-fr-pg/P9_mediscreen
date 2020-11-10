package mediscreen.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.patient.model.Patient;
import mediscreen.patient.model.PatientDisplay;
import mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static mediscreen.patient.controller.ExceptionManager.EXCEPTION_MANAGER_PATIENT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PatientRepository repository;

    @Test
    public void givenExistingPatient_whenGet_thenReturnsCorrectPatient() throws Exception {
        // GIVEN
        Patient patient = new Patient();
        patient.id = 123456789;
        PatientDisplay display = new PatientDisplay(patient);
        Optional<Patient> optional = Optional.of(patient);
        when(repository.findById(1L)).thenReturn(optional);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get("/patient/1")).andReturn().getResponse();
        PatientDisplay result = objectMapper.readValue(response.getContentAsString(), PatientDisplay.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(patient.id, result.id);
    }

    @Test
    public void givenNoPatient_whenGet_thenReturnsNotFound() throws Exception {
        // GIVEN
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get("/patient/1")).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, response.getContentAsString());
    }
}
