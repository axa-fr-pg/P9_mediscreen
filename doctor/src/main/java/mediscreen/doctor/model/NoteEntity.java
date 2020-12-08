package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
@Document(collection = "notes")
public class NoteEntity {
    @Id
    UUID noteId;

    long patId;

    String e;

    public NoteEntity(long patientId, NoteDTO note) throws IllegalArgumentException {
        this(UUID.fromString(note.noteId), patientId, note.e);
    }

    public static NoteEntity random() {
        NoteEntity note = new NoteEntity();
        note.noteId = UUID.randomUUID();
        note.patId = nextLong();
        note.e = "random";
        return note;
    }
}
