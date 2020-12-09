package mediscreen.doctor.repository;

import mediscreen.doctor.model.NoteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface NoteRepository extends MongoRepository<NoteEntity, UUID> {
}
