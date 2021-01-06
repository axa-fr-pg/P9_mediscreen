package mediscreen.doctor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class PatientNotesDTO implements Comparable<PatientNotesDTO>{
    long patId;
    List<NoteDTO> noteDTOList;

    @Override
    public int compareTo(PatientNotesDTO other) {
        return ((Long)patId).compareTo(other.patId);
    }
}
