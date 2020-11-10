package mediscreen.patient.service;

import mediscreen.patient.model.Patient;
import mediscreen.patient.model.PatientDisplay;
import mediscreen.patient.model.PatientId;
import mediscreen.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    PatientRepository repository;

    @Override
    public List<PatientDisplay> getList() {
        return repository.findAll().stream().map(patient -> new PatientDisplay(patient)).collect(Collectors.toList());
    }

    @Override
    public PatientDisplay get(long patientId) throws PatientNotFoundException {
        Optional<Patient> patient = repository.findById(patientId);
        if (patient.isPresent()) return new PatientDisplay(patient.get());
        throw new PatientNotFoundException();
    }
}
