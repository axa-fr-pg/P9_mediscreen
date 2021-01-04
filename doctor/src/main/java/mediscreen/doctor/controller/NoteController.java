package mediscreen.doctor.controller;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.PatientNotesDTO;
import mediscreen.doctor.service.CreateExistingNoteException;
import mediscreen.doctor.service.NoteNotFoundException;
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

@RestController
@RequestMapping("/notes")
@CrossOrigin
public class NoteController {

    @Autowired
    NoteService service;

    @GetMapping("")
    public ResponseEntity<List<PatientNotesDTO>> getListGroupedByPatientId() {
        return new ResponseEntity<>(service.getAllGroupedByPatientId(), HttpStatus.OK);
    }

    @GetMapping("/patients/{patId}")
    public ResponseEntity<PatientNotesDTO> getListByPatientId(@PathVariable Long patId) {
        return new ResponseEntity<>(service.getAllByPatientId(patId), HttpStatus.OK);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDTO> get(@PathVariable String noteId) throws NoteNotFoundException {
        return new ResponseEntity<>(service.get(noteId), HttpStatus.OK);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDTO> putByNoteId(@PathVariable String noteId, @RequestBody @Valid NoteDTO note) throws NoteNotFoundException {
        return new ResponseEntity<>(service.put(note), HttpStatus.OK);
    }

    @PostMapping("/patients/{patId}")
    public ResponseEntity<NoteDTO> postByPatientId(@PathVariable Long patId, @RequestBody @Valid NoteDTO e) throws CreateExistingNoteException {
        return new ResponseEntity<>(service.postNoteByPatientId(patId, e), HttpStatus.CREATED);
    }

    @PostMapping("/random/{expectedNumberOfNotes}")
    public ResponseEntity<List<NoteDTO>> post(@PathVariable Integer expectedNumberOfNotes) {
        return new ResponseEntity<>(service.post(0, expectedNumberOfNotes), HttpStatus.CREATED);
    }

    @PostMapping("/patients/{patId}/random/{expectedNumberOfNotes}")
    public ResponseEntity<List<NoteDTO>> post(@PathVariable Long patId, @PathVariable Integer expectedNumberOfNotes) {
        return new ResponseEntity<>(service.post(patId, expectedNumberOfNotes), HttpStatus.CREATED);
    }
}
