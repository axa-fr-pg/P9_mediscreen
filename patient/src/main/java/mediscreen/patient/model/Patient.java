package mediscreen.patient.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@FieldDefaults(level= AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    public long id;
}
