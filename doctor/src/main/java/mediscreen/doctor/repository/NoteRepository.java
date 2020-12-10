package mediscreen.doctor.repository;

import mediscreen.doctor.model.NoteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends MongoRepository<NoteEntity, String> {
    //     @Query("{'patId' : ?0}")
    public List<NoteEntity> findAllByPatId(long patId);
}
