package mediscreen.patient.service;

import mediscreen.patient.model.PatientEntity;
import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PatientServiceTest {

    @MockBean
    PatientRepository repository;

    @Autowired
    PatientService service;

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

    private void assertEntityEqual(PatientEntity expected, PatientEntity received) {
        assertEquals(expected.id, received.id);
        assertEquals(expected.family, received.family);
        assertEquals(expected.given, received.given);
        assertEquals(expected.dob, received.dob);
        assertEquals(expected.sex, received.sex);
        assertEquals(expected.address, received.address);
        assertEquals(expected.phone, received.phone);
    }

    @Test
    public void givenExistingPatient_whenGetPatient_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientEntity patient = mockEntityFind(46, true);
        // WHEN
        PatientDTO result = service.get(patient.id);
        // THEN
        assertEntityEqual(patient, new PatientEntity(result));
    }

    @Test
    public void givenNoPatient_whenGetPatient_thenThrowsPatientNotFoundException() {
        // GIVEN
        PatientEntity patient = mockEntityFind(56, false);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.get(patient.id));
    }

    @Test
    public void givenExistingPatient_whenPutPatient_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patientBefore = new PatientDTO(mockEntityFind(75, true));
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        // WHEN
        PatientDTO result = service.put(new PatientDTO(patientAfter));
        // THEN
        assertEntityEqual(patientAfter, new PatientEntity(result));
    }

    @Test
    public void givenNoPatient_whenPutPatient_thenThrowsPatientNotFoundException() {
        // GIVEN
        PatientEntity patient = mockEntityFind(85, false);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.put(new PatientDTO(patient)));
    }
}
