package mediscreen.doctor.service;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomUtils.nextInt;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteRepository repository;

    private final List<String> glossary;

    NoteServiceImpl() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:glossary.txt");
        Scanner scanner = new Scanner(resource.getInputStream());
        glossary = new ArrayList<>();
        while (scanner.hasNextLine()) {
            glossary.add(scanner.nextLine());
        }
    }

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
        given.noteId = UUID.randomUUID().toString();
        NoteEntity result = repository.save(given);
        return new NoteDTO(result);
    }

    @Override
    public List<NoteDTO> post(long patientId, int numberOfRows) {
        return Stream.generate(NoteEntity::random).limit(numberOfRows)
                .map(note -> {
                    if (patientId != 0) {
                        note.patId = patientId;
                    }
                    note.e = glossary.get(nextInt(0, glossary.size()));
                    return new NoteDTO(repository.save(note));
                })
                .collect(Collectors.toList());
    }
}
