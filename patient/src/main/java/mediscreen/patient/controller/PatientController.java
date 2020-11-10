package mediscreen.patient.controller;

import mediscreen.patient.model.Patient;
import mediscreen.patient.model.PatientDisplay;
import mediscreen.patient.service.PatientNotFoundException;
import mediscreen.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/patient")
@CrossOrigin
public class PatientController {
    @Autowired
    PatientService service;

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDisplay> get(@PathVariable long patientId) throws PatientNotFoundException {
        return new ResponseEntity<PatientDisplay>(service.get(patientId), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List> getList() {
        return new ResponseEntity<List>(service.getList(), HttpStatus.OK);
    }
}
