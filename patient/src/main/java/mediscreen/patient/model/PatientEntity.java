package mediscreen.patient.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@Entity
@Table(name="patient")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class PatientEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id;
    String family;
    String given;
    LocalDate dob;
    String sex;
    String address;
    String phone;

    public PatientEntity(PatientDTO patient) {
        this(patient.id, patient.family, patient.given, patient.dob, patient.sex, patient.address, patient.phone);
    }

    public static PatientEntity random() {
        PatientEntity patient = new PatientEntity();
        patient.family = randomAlphabetic(nextInt(3, 15));
        patient.given = randomAlphabetic(nextInt(3, 15));
        patient.dob = LocalDate.of(nextInt(1900, 2019), nextInt(1, 12), nextInt(1, 28));
        patient.sex = nextBoolean() ? "F" : "M";
        patient.address = randomAlphabetic(nextInt(3, 30));
        patient.phone = randomNumeric(nextInt(1, 3)) + "-" + randomNumeric(nextInt(3, 6)) + "-" + randomNumeric(nextInt(3, 6));
        return patient;
    }
}
