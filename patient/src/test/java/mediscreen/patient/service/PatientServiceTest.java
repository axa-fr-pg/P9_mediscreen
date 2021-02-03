package mediscreen.patient.service;

import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.model.PatientEntity;
import mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    private PageImpl<PatientEntity> mockEntityFindAll()  {
        PatientEntity patient1 = PatientEntity.random();
        PatientEntity patient2 = PatientEntity.random();
        PageImpl<PatientEntity> page = new PageImpl<>(Arrays.asList(patient1, patient2));
        List<PatientEntity> list = new ArrayList(Arrays.asList(patient1, patient2));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(repository.findAll()).thenReturn(list);
        return page;
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
    public void givenExistingPatient_whenGet_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientEntity patient = mockEntityFind(46, true);
        // WHEN
        PatientDTO result = service.get(patient.id);
        // THEN
        assertEntityEqual(patient, new PatientEntity(result));
    }

    @Test
    public void givenNoPatient_whenGet_thenThrowsPatientNotFoundException() {
        // GIVEN
        PatientEntity patient = mockEntityFind(56, false);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.get(patient.id));
    }

    @Test
    public void givenExistingPatient_whenPut_thenReturnsCorrectPatient() throws PatientNotFoundException {
        // GIVEN
        PatientDTO patientBefore = new PatientDTO(mockEntityFind(75, true));
        PatientEntity patientAfter = mockEntitySave(patientBefore.id);
        // WHEN
        PatientDTO result = service.put(new PatientDTO(patientAfter));
        // THEN
        assertEntityEqual(patientAfter, new PatientEntity(result));
    }

    @Test
    public void givenNoPatient_whenPut_thenThrowsPatientNotFoundException() {
        // GIVEN
        PatientEntity patient = mockEntityFind(85, false);
        // WHEN
        // THEN
        assertThrows(PatientNotFoundException.class, () -> service.put(new PatientDTO(patient)));
    }

    @Test
    public void givenPatientWithId_whenPost_thenThrowsCreateExistingPatientException() {
        // GIVEN
        PatientEntity patient = new PatientEntity();
        patient.sex = "M";
        patient.id = 1;
        // WHEN
        // THEN
        assertThrows(CreateExistingPatientException.class, () -> service.post(new PatientDTO(patient)));
    }

    @Test
    public void givenPatientSameFamilyAndDob_whenPost_thenThrowsCreateExistingPatientException() {
        // GIVEN
        PatientEntity patient = mockEntityFind(112, true);
        when(repository.findByFamilyAndDob(patient.family, patient.dob)).thenReturn(Collections.singletonList(patient));
        PatientEntity duplicate = new PatientEntity(new PatientDTO(patient));
        duplicate.id = 0;
        duplicate.family = patient.family;
        duplicate.dob = patient.dob;
        // WHEN
        // THEN
        assertThrows(CreateExistingPatientException.class, () -> service.post(new PatientDTO(duplicate)));
    }

    @Test
    public void givenNewPatient_whenPost_thenReturnsCorrectPatient() throws CreateExistingPatientException {
        // GIVEN
        PatientEntity patient = mockEntityCreate();
        PatientEntity request = new PatientEntity(new PatientDTO(patient));
        request.id = 0;
        // WHEN
        PatientDTO result = service.post(new PatientDTO(request));
        // THEN
        assertEntityEqual(patient, new PatientEntity(result));
    }

    @Test
    public void givenRandomRequest_whenPost_thenReturnsListOfCorrectSize() {
        // GIVEN
        mockEntityCreate();
        int expectedNumberOfPatients = 5;
        // WHEN
        List<PatientDTO> result = service.post(expectedNumberOfPatients);
        // THEN
        assertEquals(expectedNumberOfPatients, result.size());
    }
    @Test
    public void givenList_whenGetList_thenReturnsCorrectList() {
        // GIVEN
        Page<PatientEntity> givenPage = mockEntityFindAll();
        // WHEN
        List<PatientDTO> result = service.getList();
        // THEN
        assertEquals(givenPage.toList().size(), result.size());
    }

    @Test
    public void givenPage_whenGetPageSortById_thenReturnsCorrectPage() {
        // GIVEN
        int pageNumber = 2;
        Page<PatientEntity> givenPage = mockEntityFindAll();
        // WHEN
        Page<PatientDTO> result = service.getPage(PageRequest.of(0,100));
        // THEN
        assertEquals(givenPage.getNumber(), result.getNumber());
        assertEquals(givenPage.toList().size(), result.toList().size());
    }
}
