package mediscreen.patient.service;

import mediscreen.patient.model.PatientDTO;
import mediscreen.patient.model.PatientEntity;
import mediscreen.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    PatientRepository patientRepository;

    @Override
    public List<PatientDTO> getList() {
        return patientRepository.findAll().stream().map(PatientDTO::new).collect(Collectors.toList());
    }

    @Override
    public Page<PatientDTO> getPage(Pageable pageRequest) {
        return patientRepository.findAll(pageRequest).map(PatientDTO::new);
    }

    @Override
    public PatientDTO get(long patientId) throws PatientNotFoundException {
        Optional<PatientEntity> patient = patientRepository.findById(patientId);
        if (patient.isPresent()) return new PatientDTO(patient.get());
        throw new PatientNotFoundException();
    }

    @Override
    public PatientDTO put(PatientDTO patient) throws PatientNotFoundException {
        Optional<PatientEntity> optional = patientRepository.findById(patient.id);
        if (optional.isPresent()) {
            return new PatientDTO(patientRepository.save(new PatientEntity(patient)));
        }
        throw new PatientNotFoundException();
    }

    @Override
    public PatientDTO post(PatientDTO patient) throws CreateExistingPatientException {
        if (patient.id != 0) throw new CreateExistingPatientException();
        List<PatientEntity> existingPatients = patientRepository.findByFamilyAndDob(patient.family, patient.dob);
        if (existingPatients.size() > 0) throw new CreateExistingPatientException();
        PatientEntity result = patientRepository.save(new PatientEntity(patient));
        return new PatientDTO(result);
    }

    @Override
    public List<PatientDTO> post(int numberOfRows) {
        return Stream.generate(PatientEntity::random).limit(numberOfRows)
                .map(patient -> new PatientDTO(patientRepository.save(patient)))
                .collect(Collectors.toList());
    }
}
