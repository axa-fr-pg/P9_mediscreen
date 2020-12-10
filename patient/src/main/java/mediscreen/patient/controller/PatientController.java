package mediscreen.patient.controller;

import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.service.CreateExistingPatientException;
import mediscreen.patient.service.PatientNotFoundException;
import mediscreen.patient.service.PatientService;
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
@RequestMapping("/patients")
@CrossOrigin
public class PatientController {
    @Autowired
    PatientService service;

    @GetMapping("")
    public ResponseEntity<List<PatientDTO>> getList() {
        return new ResponseEntity<>(service.getList(), HttpStatus.OK);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDTO> get(@PathVariable Long patientId)
            throws PatientNotFoundException {
        return new ResponseEntity<>(service.get(patientId), HttpStatus.OK);
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<PatientDTO> put(@PathVariable Long patientId, @RequestBody @Valid PatientDTO patient)
            throws PatientNotFoundException {
        if (patientId != patient.id) throw new PatientNotFoundException();
        return new ResponseEntity<>(service.put(patient), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<PatientDTO> post(@RequestBody @Valid PatientDTO patient) throws CreateExistingPatientException {
        return new ResponseEntity<>(service.post(patient), HttpStatus.CREATED);
    }

    @PostMapping("/random/{expectedNumberOfPatients}")
    public ResponseEntity<List<PatientDTO>> post(@PathVariable Integer expectedNumberOfPatients) throws CreateExistingPatientException {
        return new ResponseEntity<>(service.post(expectedNumberOfPatients), HttpStatus.CREATED);
    }
}
