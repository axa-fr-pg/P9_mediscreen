package mediscreen.patient.service;

import mediscreen.patient.model.PatientEntity;
import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    PatientRepository repository;

    @Override
    public List<PatientDTO> getList() {
        return repository.findAll().stream().map(PatientDTO::new).collect(Collectors.toList());
    }

    @Override
    public PatientDTO get(long patientId) throws PatientNotFoundException {
        Optional<PatientEntity> patient = repository.findById(patientId);
        if (patient.isPresent()) return new PatientDTO(patient.get());
        throw new PatientNotFoundException();
    }

    @Override
    public PatientDTO put(PatientDTO patient) throws PatientNotFoundException {
        Optional<PatientEntity> optional = repository.findById(patient.id);
        if (optional.isPresent()) {
            return new PatientDTO(repository.save(new PatientEntity(patient)));
        }
        throw new PatientNotFoundException();
    }

    @Override
    public PatientDTO post(PatientDTO patient) throws CreateExistingPatientException {
        if (patient.id != 0) throw new CreateExistingPatientException();
        List<PatientEntity> existingPatients = repository.findByFamilyAndDob(patient.family, patient.dob);
        if (existingPatients.size() > 0) throw new CreateExistingPatientException();
        PatientEntity result = repository.save(new PatientEntity(patient));
        return new PatientDTO(result);
    }

    @Override
    public List<PatientDTO> post(int numberOfRows) {
        return Stream.generate(PatientEntity::random).limit(numberOfRows)
                .map(patient -> new PatientDTO(repository.save(patient)))
                .collect(Collectors.toList());
    }
}
