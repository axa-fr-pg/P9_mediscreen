package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;

import java.util.List;

public interface NoteService {
    List<NoteDTO> getList();
    NoteDTO postNoteByPatientId(long patientId, NoteDTO note) throws CreateExistingNoteException;
}
