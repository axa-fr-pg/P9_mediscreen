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
import java.util.Date;

@Entity
@Table(name="patient")
//@AllArgsConstructor
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
}
