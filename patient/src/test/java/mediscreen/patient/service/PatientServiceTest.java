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

    private PatientEntity mockEntity(long id, boolean exists)  {
        PatientEntity patient = PatientEntity.random();
        patient.id = id;
        PatientDTO display = new PatientDTO(patient);
        Optional<PatientEntity> optional = exists ? Optional.of(patient) : Optional.empty();
        when(repository.findById(id)).thenReturn(optional);
        when(repository.save(patient)).thenReturn(patient);
        return patient;
    }

    @Test
    public void givenExistingPatient_whenGetPatient_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientEntity patient = mockEntity(46, true);
        // WHEN
        PatientDTO result = service.get(patient.id);
        // THEN
        assertEquals(patient.id, result.id);
    }

    @Test
    public void givenNoPatient_whenGetPatient_thenThrowsPatientNotFoundException() {
        // GIVEN
        PatientEntity patient = mockEntity(56, false);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.get(patient.id));
    }

    @Test
    public void givenExistingPatient_whenPutPatient_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patient = new PatientDTO(mockEntity(65, true));
        // WHEN
        PatientDTO result = service.put(patient);
        // THEN
        assertEquals(patient.id, result.id);
    }

    @Test
    public void givenNoPatient_whenPutPatient_thenThrowsPatientNotFoundException() {
        // GIVEN
        PatientEntity patient = mockEntity(75, false);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.put(new PatientDTO(patient)));
    }
}
