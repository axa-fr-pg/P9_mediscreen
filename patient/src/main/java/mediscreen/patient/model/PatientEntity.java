package mediscreen.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

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
    Date dob;
    String sex;
    String address;
    String phone;

    public PatientEntity(PatientDTO patient) {
        this(patient.id, patient.family, patient.given, patient.dob, patient.sex, patient.address, patient.phone);
    }

    public static PatientEntity random() {
        PatientEntity patient = new PatientEntity();
        patient.family = randomAlphabetic(nextInt(3, 10));
        patient.given = randomAlphabetic(nextInt(3, 10));
        patient.dob = new Date(nextLong(0, 946681200000L));
        patient.sex = nextBoolean() ? "F" : "M";
        patient.address = randomAlphabetic(nextInt(3, 10));
        patient.phone = randomNumeric(nextInt(1, 3)) + "-" + randomNumeric(nextInt(2, 4)) + "-" + randomNumeric(nextInt(1, 3));
        return patient;
    }
}
