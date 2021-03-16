package mediscreen.patient.controller;

import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.model.PatientEntity;
import mediscreen.patient.service.CreateExistingPatientException;
import mediscreen.patient.service.PatientNotFoundException;
import mediscreen.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PatientControllerTest {

    @MockBean
    PatientService service;

    @Autowired
    PatientController controller;

    @BeforeEach
    public void init() {
        reset(service);
    }

    private PatientDTO mockServiceGet(long id, boolean exists) throws PatientNotFoundException {
        PatientDTO patient = new PatientDTO(PatientEntity.random());
        patient.id = id;
        if (exists) {
            when(service.get(id)).thenReturn(patient);
        } else {
            when(service.get(id)).thenThrow(PatientNotFoundException.class);
        }
        return patient;
    }

    private PatientDTO mockServicePut(long id, boolean exists) throws PatientNotFoundException {
        PatientDTO patient = new PatientDTO(PatientEntity.random());
        patient.id = id;
        if (exists) {
            when(service.put(any(PatientDTO.class))).thenReturn(patient);
        } else {
            when(service.put(any(PatientDTO.class))).thenThrow(PatientNotFoundException.class);
        }
        return patient;
    }

    private PatientDTO mockServicePost(boolean exists) throws CreateExistingPatientException {
        PatientDTO patient = new PatientDTO(PatientEntity.random());
        patient.id = new Random().nextLong();
        if (exists) {
            when(service.post(any(PatientDTO.class))).thenThrow(CreateExistingPatientException.class);
        } else {
            when(service.post(any(PatientDTO.class))).thenReturn(patient);
        }
        return patient;
    }

    @Test
    public void givenExistingPatient_whenGet_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patient = mockServiceGet(102, true);
        // WHEN
        ResponseEntity<PatientDTO> response = controller.get(patient.id);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(patient.id, response.getBody().id);
    }

    @Test
    public void givenNoPatient_whenGet_thenThrowsNotFound() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patient = mockServiceGet(114, false);
        // WHEN & THEN
        assertThrows(PatientNotFoundException.class, () -> controller.get(patient.id));
    }

    @Test
    public void givenExistingPatient_whenPut_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patient = mockServicePut(126, true);
        // WHEN
        ResponseEntity<PatientDTO> response = controller.put(patient.id, patient);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(patient.id, response.getBody().id);
    }

    @Test
    public void givenNoPatient_whenPut_thenThrowsNotFound() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patient = mockServicePut(154, false);
        // WHEN & THEN
        assertThrows(PatientNotFoundException.class, () -> controller.put(patient.id, patient));
    }

    @Test
    public void givenPatientSameFamilyAndDob_whenPost_thenThrowsCreateExistingPatient() throws CreateExistingPatientException {
        // GIVEN
        PatientDTO patient = mockServicePost(true);
        // WHEN & THEN
        assertThrows(CreateExistingPatientException.class, () -> controller.post(patient));
    }

    @Test
    public void givenNewPatient_whenPost_thenReturnsCorrectPatient() throws CreateExistingPatientException {
        // GIVEN
        PatientDTO patient = mockServicePost(false);
        // WHEN
        ResponseEntity<PatientDTO> response = controller.post(patient);
        // THEN
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(patient.id, response.getBody().id);
    }
}
