package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
@Document(collection = "notes")
public class NoteEntity {
    @Id
    String noteId;

    long patId;

    String e;

    public NoteEntity(long patientId, NoteDTO note) throws IllegalArgumentException {
        this(note.noteId, patientId, note.e);
    }

    public static NoteEntity random() {
        NoteEntity note = new NoteEntity();
        note.noteId = UUID.randomUUID().toString();
        note.patId = nextLong(1, 100);
        note.e = randomAlphabetic(nextInt(20, 500));
        return note;
    }
}
