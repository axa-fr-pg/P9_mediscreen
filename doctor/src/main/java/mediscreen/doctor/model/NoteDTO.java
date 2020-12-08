package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class NoteDTO {

    String noteId;

    @NotBlank(message="A DEFINIR")
    String e;

    public NoteDTO(NoteEntity note) {
        this(note.noteId.toString(), note.e);
    }
}
