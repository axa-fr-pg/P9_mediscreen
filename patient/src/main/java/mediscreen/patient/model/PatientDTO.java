package mediscreen.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

import static mediscreen.patient.model.PatientValidation.ADDRESS_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientValidation.DOB_NOT_NULL_ERROR;
import static mediscreen.patient.model.PatientValidation.DOB_PAST_ERROR;
import static mediscreen.patient.model.PatientValidation.FAMILY_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientValidation.GIVEN_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientValidation.PHONE_NOT_BLANK_ERROR;
import static mediscreen.patient.model.PatientValidation.PHONE_PATTERN_ERROR;
import static mediscreen.patient.model.PatientValidation.SEX_NOT_NULL_ERROR;
import static mediscreen.patient.model.PatientValidation.SEX_PATTERN_ERROR;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class PatientDTO {
    long id;

    @NotBlank(message=FAMILY_NOT_BLANK_ERROR)
    String family;

    @NotBlank(message=GIVEN_NOT_BLANK_ERROR)
    String given;

    @NotNull(message=DOB_NOT_NULL_ERROR)
    @Past(message=DOB_PAST_ERROR)
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate dob;

    @NotNull(message=SEX_NOT_NULL_ERROR)
    @Pattern(regexp = "^[m|M|f|F]{1}$", message=SEX_PATTERN_ERROR)
    String sex;

    @NotBlank(message=ADDRESS_NOT_BLANK_ERROR)
    String address;

    @NotBlank(message=PHONE_NOT_BLANK_ERROR)
    @Pattern(regexp = ".*[- +()0-9]", message=PHONE_PATTERN_ERROR)
    String phone;

    public PatientDTO(PatientEntity patient) {
        this(patient.id, patient.family, patient.given, patient.dob, patient.sex.toUpperCase(), patient.address, patient.phone);
    }
}
