package mediscreen.patient.controller;

import mediscreen.patient.model.Patient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PatientController {

    @PostMapping("/hello")
    public ResponseEntity<Patient> hello(@RequestBody @Valid Patient patient) {
        return new ResponseEntity<Patient>(patient, HttpStatus.OK);
    }
}
