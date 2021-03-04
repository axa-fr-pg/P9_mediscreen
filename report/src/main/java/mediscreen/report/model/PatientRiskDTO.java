package mediscreen.report.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PUBLIC)
public class PatientRiskDTO {
    long id;
    String family;
    String risk;
}
