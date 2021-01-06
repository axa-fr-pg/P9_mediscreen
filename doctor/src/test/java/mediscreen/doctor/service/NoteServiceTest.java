package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.model.PatientNotesDTO;
import mediscreen.doctor.repository.NoteRepository;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NoteServiceTest {

    @MockBean
    NoteRepository repository;

    @Autowired
    NoteService service;

    private NoteEntity mockEntityFind(boolean exists)  {
        NoteEntity note = NoteEntity.random();
        Optional<NoteEntity> optional = exists ? Optional.of(note) : Optional.empty();
        when(repository.findById(note.noteId)).thenReturn(optional);
        return note;
    }

    private List<NoteEntity> mockEntityFindAllByPatId(long patientId, int numberOfEntities) {
        List<NoteEntity> noteEntityList = Stream.generate(() -> NoteEntity.random(patientId))
                .limit(numberOfEntities).collect(Collectors.toList());
        when(repository.findAllByPatId(patientId)).thenReturn(noteEntityList);
        return noteEntityList;
    }

    private List<PatientNotesDTO> mockEntityFindAllGroupedByPatientId() {
        List<PatientNotesDTO> result = new ArrayList<>();
        List<NoteEntity> allNotes = new ArrayList<>();
        long patientId = nextInt(100, 200);
        for (int branch=0; branch< nextInt(3, 15) ; branch++) {
            List<NoteDTO> branchNotes = new ArrayList<>();
            patientId += nextInt(1, 20);
            for (int index=0; index < (branch + nextInt(2, 10)); index++) {
                NoteEntity noteEntity = NoteEntity.random(patientId);
                NoteDTO noteDTO = new NoteDTO(noteEntity);
                allNotes.add(noteEntity);
                branchNotes.add(noteDTO);
            }
            result.add(new PatientNotesDTO(patientId, branchNotes));
        }
        when(repository.findAllByNoteIdNotNullOrderByPatIdAsc()).thenReturn(allNotes);
        return result;
    }

    private NoteEntity mockEntityCreate()  {
        NoteEntity note = NoteEntity.random();
        when(repository.save(any(NoteEntity.class))).thenReturn(note);
        return note;
    }

    private NoteEntity mockEntitySave(String noteId)  {
        NoteEntity note = NoteEntity.random();
        note.noteId = noteId;
        when(repository.save(any(NoteEntity.class))).thenReturn(note);
        return note;
    }

    @BeforeEach
    public void init() {
        reset(repository);
    }

    @Test
    public void givenExistingNote_whenGet_thenReturnsCorrectNote() throws NoteNotFoundException {
        // GIVEN
        NoteEntity note = mockEntityFind(true);
        // WHEN
        NoteDTO result = service.get(note.noteId);
        // THEN
        assertEquals(note.noteId, result.noteId);
        assertEquals(note.e, result.e);
    }

    @Test
    public void givenMissingNote_whenGet_thenThrowsNoteNotFoundException() {
        // GIVEN
        NoteEntity note = mockEntityFind(false);
        // WHEN
        // THEN
        assertThrows(NoteNotFoundException.class, () -> service.get(note.noteId));
    }

    @Test
    public void givenPatientId_whenGetAllByPatientId_thenReturnsCorrectListOfNotes() {
        // GIVEN
        long patientId = 123456;
        int numberOfEntities = 5;
        List<NoteEntity> noteEntityList = mockEntityFindAllByPatId(patientId, numberOfEntities);
        // WHEN
        PatientNotesDTO result = service.getAllByPatientId(patientId);
        // THEN
        assertEquals(patientId, result.patId);
        assertEquals(numberOfEntities, result.noteDTOList.size());
    }

    @Test
    public void givenNoPatientId_whenGetAllGroupedByPatientId_thenReturnsCorrectTreeOfNotes() {
        // GIVEN
        long sortingPatId = -1;
        List<PatientNotesDTO> patientNotesDTOList = mockEntityFindAllGroupedByPatientId();
        // WHEN
        List<PatientNotesDTO> result = service.getAllGroupedByPatientId();
        // THEN
        assertEquals(patientNotesDTOList.size(), result.size());
        patientNotesDTOList.forEach(patientNotesDTO -> {
                assertTrue(sortingPatId <= patientNotesDTO.patId);
                PatientNotesDTO match = IterableUtils.find(result, current -> current.patId == patientNotesDTO.patId);
                assertNotNull(match);
                assertEquals(patientNotesDTO.noteDTOList.size(), match.noteDTOList.size());
            }
        );
    }

    @Test
    public void givenPatientIdAndNewNote_whenPostNoteByPatientId_thenReturnsCorrectNote() throws CreateExistingNoteException {
        // GIVEN
        long patientId = 123456;
        NoteEntity note = mockEntityCreate();
        NoteDTO request = new NoteDTO(note);
        request.noteId = null;
        // WHEN
        NoteDTO result = service.postNoteByPatientId(patientId, request);
        // THEN
        assertEquals(note.noteId, result.noteId);
        assertEquals(note.e, result.e);
    }

    @Test
    public void givenNonEmptyNoteId_whenPostPatient_thenThrowsCreateExistingNoteException() {
        // GIVEN
        NoteEntity note = mockEntityCreate();
        note.noteId = "non empty";
        // WHEN
        // THEN
        assertThrows(CreateExistingNoteException.class, () -> service.postNoteByPatientId(0, new NoteDTO(note)));
    }

    @Test
    public void givenRandomRequest_whenPost_thenReturnsListOfCorrectSize() {
        // GIVEN
        long patientId = 123456;
        mockEntityCreate();
        int expectedNumberOfNotes = 5;
        // WHEN
        List<NoteDTO> result = service.post(patientId, expectedNumberOfNotes);
        // THEN
        assertEquals(expectedNumberOfNotes, result.size());
    }

    @Test
    public void givenExistingNote_whenPut_thenReturnsCorrectNote() throws NoteNotFoundException {
        // GIVEN
        NoteDTO noteBefore = new NoteDTO(mockEntityFind(true));
        NoteEntity noteAfter = mockEntitySave(noteBefore.noteId);
        // WHEN
        NoteDTO result = service.put(new NoteDTO(noteAfter));
        // THEN
        assertEquals(noteAfter.noteId, result.noteId);
        assertEquals(noteAfter.e, result.e);
    }

    @Test
    public void givenNoNote_whenPut_thenThrowsNoteNotFoundException() {
        // GIVEN
        NoteEntity note = mockEntityFind(false);
        // WHEN
        // THEN
        assertThrows(NoteNotFoundException.class, () -> service.put(new NoteDTO(note)));
    }
}
