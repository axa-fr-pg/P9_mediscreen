package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;

import java.util.List;

public interface NoteService {
    List<NoteDTO> getAll();
    List<NoteDTO> getAllByPatientId(long patientId);
    NoteDTO get(String noteId) throws NoteNotFoundException;
    NoteDTO postNoteByPatientId(long patientId, NoteDTO note) throws CreateExistingNoteException;
    List<NoteDTO> post(long patientId, int numberOfRandomRows);

}
