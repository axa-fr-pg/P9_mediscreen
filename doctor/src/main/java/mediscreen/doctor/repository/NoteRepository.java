package mediscreen.doctor.repository;

import mediscreen.doctor.model.NoteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface NoteRepository extends MongoRepository<NoteEntity, UUID> {
    List<NoteEntity> findByFamilyAndDob(String family, LocalDate dob);
}
