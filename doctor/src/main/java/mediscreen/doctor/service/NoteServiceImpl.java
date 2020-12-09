package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    NoteRepository repository;

    @Override
    public List<NoteDTO> getList() {
//        return repository.findAll().stream().map(NoteDTO::new).collect(Collectors.toList());
        return Arrays.asList(new NoteDTO(), new NoteDTO(), new NoteDTO());
    }

    @Override
    public NoteDTO postNoteByPatientId(long patientId, NoteDTO note) throws CreateExistingNoteException {
        if (note.noteId != null && note.noteId.length() > 0) {
            throw new CreateExistingNoteException();
        }
        NoteEntity given = new NoteEntity(patientId, note);
        NoteEntity result = repository.save(given);
        return new NoteDTO(result);
    }
}
