package mediscreen.report.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PUBLIC)
public class PatientData {
    long id;
    String family;
    String given;
    LocalDate dob;
    String sex;
    String address;
    String phone;

    public long getId() {
        return id;
    }
}
