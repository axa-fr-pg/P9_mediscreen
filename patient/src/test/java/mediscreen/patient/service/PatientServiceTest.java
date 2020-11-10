package mediscreen.patient.service;

import mediscreen.patient.model.Patient;
import mediscreen.patient.model.PatientDisplay;
import mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PatientServiceTest {

    @MockBean
    PatientRepository repository;

    @Autowired
    PatientService service;

    @Test
    public void givenExistingPatient_whenGetPatient_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        Patient patient = new Patient();
        patient.id = 123456789;
        PatientDisplay display = new PatientDisplay(patient);
        Optional<Patient> optional = Optional.of(patient);
        when(repository.findById(1L)).thenReturn(optional);
        // WHEN
        PatientDisplay result = service.get(1L);
        // THEN
        assertEquals(patient.id, result.id);
    }

    @Test
    public void givenNoPatient_whenGetPatient_thenThrowsPatientNotFoundException() {
        // GIVEN
        Optional<Patient> optional = Optional.empty();
        when(repository.findById(1L)).thenReturn(optional);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.get(1L));
    }
}
