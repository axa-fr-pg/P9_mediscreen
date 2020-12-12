package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class PatientNotesDTO {
    long patId;
    List<NoteDTO> noteDTOList;
}
