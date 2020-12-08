package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NoteServiceTest {

    @MockBean
    NoteRepository repository;

    @Autowired
    NoteService service;

    private NoteEntity mockEntityCreate()  {
        NoteEntity note = NoteEntity.random();
        when(repository.save(any(NoteEntity.class))).thenReturn(note);
        return note;
    }
    
    @BeforeEach
    public void init() {
        reset(repository);
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
        assertEquals(note.noteId.toString(), result.noteId);
        assertEquals(note.e, result.e);
    }

    @Test
    public void givenNoteId_whenPostPatient_thenThrowsCreateExistingNoteException() {
        // GIVEN
        NoteEntity note = new NoteEntity();
        note.noteId = UUID.randomUUID();
        // WHEN
        // THEN
        assertThrows(CreateExistingNoteException.class, () -> service.postNoteByPatientId(0, new NoteDTO(note)));
    }
}
