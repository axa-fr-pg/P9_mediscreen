package mediscreen.patient.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@FieldDefaults(level=AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @NotNull(message="Patient ID is mandatory")
    @Positive(message="Patient ID must be positive")
    public long id;

    @NotBlank(message="Family name must contain at least one non-whitespace character")
    String family;
}
