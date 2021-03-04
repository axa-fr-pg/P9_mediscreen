package mediscreen.doctor.controller;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.model.PatientNotesDTO;
import mediscreen.doctor.service.CreateExistingNoteException;
import mediscreen.doctor.service.NoteNotFoundException;
import mediscreen.doctor.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NoteControllerTest {

    @MockBean
    NoteService service;

    @Autowired
    NoteController controller;

    @BeforeEach
    public void init() {
        reset(service);
    }

    private NoteDTO mockServiceGet(String noteId, boolean exists) throws NoteNotFoundException {
        NoteDTO note = new NoteDTO(NoteEntity.random());
        note.noteId = noteId;
        if (exists) {
            when(service.get(noteId)).thenReturn(note);
        } else {
            when(service.get(noteId)).thenThrow(NoteNotFoundException.class);
        }
        return note;
    }

    private NoteDTO mockServicePut(String noteId, boolean exists) throws NoteNotFoundException {
        NoteDTO note = new NoteDTO(NoteEntity.random());
        note.noteId = noteId;
        if (exists) {
            when(service.put(any(NoteDTO.class))).thenReturn(note);
        } else {
            when(service.put(any(NoteDTO.class))).thenThrow(NoteNotFoundException.class);
        }
        return note;
    }

    private NoteDTO mockServicePost(boolean exists) throws CreateExistingNoteException {
        NoteDTO note = new NoteDTO(NoteEntity.random());
        if (exists) {
            when(service.postNoteByPatientId(anyLong(), any(NoteDTO.class))).thenThrow(CreateExistingNoteException.class);
        } else {
            when(service.postNoteByPatientId(anyLong(), any(NoteDTO.class))).thenReturn(note);
        }
        return note;
    }

    @Test
    public void givenExistingNote_whenGet_thenReturnsCorrectNote() throws NoteNotFoundException {
        // GIVEN
        NoteDTO note = mockServiceGet("102", true);
        // WHEN
        ResponseEntity<NoteDTO> response = controller.get(note.noteId);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(note.noteId, response.getBody().noteId);
    }

    @Test
    public void givenNoPatient_whenGet_thenThrowsNotFound() throws NoteNotFoundException {
        // GIVEN
        NoteDTO note = mockServiceGet("85", false);
        // WHEN & THEN
        assertThrows(NoteNotFoundException.class, () -> controller.get(note.noteId));
    }

    @Test
    public void givenExistingPatient_whenPut_thenReturnsCorrectPatient() throws NoteNotFoundException {
        // GIVEN
        NoteDTO note = mockServicePut("126", true);
        // WHEN
        ResponseEntity<NoteDTO> response = controller.put(note.noteId, note);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(note.noteId, response.getBody().noteId);
    }

    @Test
    public void givenNoPatient_whenPut_thenThrowsNotFound() throws NoteNotFoundException {
        // GIVEN
        NoteDTO note = mockServicePut("154", false);
        // WHEN & THEN
        assertThrows(NoteNotFoundException.class, () -> controller.put(note.noteId, note));
    }

    @Test
    public void givenPatientSameFamilyAndDob_whenPost_thenThrowsCreateExistingPatientError() throws CreateExistingNoteException {
        // GIVEN
        NoteDTO note = mockServicePost(true);
        // WHEN & THEN
        assertThrows(CreateExistingNoteException.class, () -> controller.postByPatientId(123456L, note));
    }

    @Test
    public void givenNewPatient_whenPost_thenReturnsCorrectPatient() throws CreateExistingNoteException {
        // GIVEN
        NoteDTO note = mockServicePost(false);
        // WHEN
        ResponseEntity<NoteDTO> response = controller.postByPatientId(123456L, note);
        // THEN
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(note.noteId, response.getBody().noteId);
    }

    @Test
    public void test_getPageGroupedByPatientId_ok() {
        // GIVEN
        when(service.getPageSortByPatientId(any(Pageable.class), anyString()))
                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 2), 0));
        // WHEN
        ResponseEntity<Page<PatientNotesDTO>> response = controller.getPageGroupedByPatientId(null, null);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }
}
