package mediscreen.doctor.controller;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@CrossOrigin
public class NoteController {

    @Autowired
    NoteService service;

    @GetMapping("/patient/{patId}")
    public ResponseEntity<List<NoteDTO>> getListByPatientId(@PathVariable Long patId) {
        return new ResponseEntity<>(service.getList(), HttpStatus.OK);
    }

    @PutMapping("/note/{noteId}")
    public ResponseEntity<NoteDTO> putByNoteId(@PathVariable String noteId, @RequestBody @Valid NoteDTO e) {
        return new ResponseEntity<NoteDTO>(new NoteDTO(), HttpStatus.OK);
    }

    @PostMapping("/patient/{patId}")
    public ResponseEntity<NoteDTO> postByPatientId(@PathVariable Long patId, @RequestBody @Valid NoteDTO e) {
        return new ResponseEntity<NoteDTO>(new NoteDTO(), HttpStatus.CREATED);
    }
}
