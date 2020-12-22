package mediscreen.doctor.repository;

import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.model.PatientNotesDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends MongoRepository<NoteEntity, String> {
    public List<NoteEntity> findAllByPatId(long patId);
    public List<NoteEntity> findByOrOrderByPatIdAsc();
}
