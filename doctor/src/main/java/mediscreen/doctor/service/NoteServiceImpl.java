package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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
}
