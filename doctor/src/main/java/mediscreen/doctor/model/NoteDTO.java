package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

import static mediscreen.doctor.model.NoteValidation.NOTE_NOT_BLANK_ERROR;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class NoteDTO {

    String noteId;

    @NotBlank(message=NOTE_NOT_BLANK_ERROR)
    String e;

    public NoteDTO(NoteEntity note) {
        this(note.noteId, note.e);
    }
}
