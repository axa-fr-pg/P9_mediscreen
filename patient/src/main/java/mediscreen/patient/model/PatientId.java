package mediscreen.patient.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Positive;

public class PatientId {
    @Positive(message="Id must be a positive number")
    public long id;
}
