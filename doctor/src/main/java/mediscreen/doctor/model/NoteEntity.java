package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class NoteEntity {
    @Id
    UUID noteId;

    long patId;

    String e;

    public NoteEntity(NoteDTO note) throws IllegalArgumentException {
        this(UUID.fromString(note.noteId), 0, note.e);
    }

    public static NoteEntity random() {
        NoteEntity patient = new NoteEntity();
/*        patient.family = randomAlphabetic(nextInt(3, 15));
        patient.given = randomAlphabetic(nextInt(3, 15));
        patient.dob = LocalDate.of(nextInt(1900, 2019), nextInt(1, 12), nextInt(1, 28));
        patient.sex = nextBoolean() ? "F" : "M";
        patient.address = randomAlphabetic(nextInt(3, 30));
        patient.phone = randomNumeric(nextInt(1, 3)) + "-" + randomNumeric(nextInt(3, 6)) + "-" + randomNumeric(nextInt(3, 6));*/
        return patient;
    }
}
