package mediscreen.patient.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class PatientDisplay {
    long id;
    String family;
    String given;
    Date dob;
    String sex;
    String address;
    String phone;

    public PatientDisplay(Patient patient) {
        this(patient.id, patient.family, patient.given, patient.dob, patient.sex, patient.address, patient.phone);
    }
}
