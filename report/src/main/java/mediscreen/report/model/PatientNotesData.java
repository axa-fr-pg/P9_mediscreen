package mediscreen.report.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PUBLIC)
public class PatientNotesData {
    Long patId;
    List<NoteData> noteDTOList;
}
