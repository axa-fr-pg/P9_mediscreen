package mediscreen.doctor.controller;

import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notes")
@CrossOrigin
public class NoteController {

    @Autowired
    NoteService service;

    @GetMapping("")
    public ResponseEntity<List<NoteDTO>> getList() {
        return new ResponseEntity<>(service.getList(), HttpStatus.OK);
    }
}
