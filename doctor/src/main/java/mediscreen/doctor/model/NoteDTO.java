package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class NoteDTO {

    public final static String NOTE_NOT_BLANK_ERROR = "You must enter some text for your note. Please check your request and try again.";

    String noteId;

    @NotBlank(message=NOTE_NOT_BLANK_ERROR)
    String e;

    public NoteDTO(NoteEntity note) {
        this(note.noteId, note.e);
    }
}
