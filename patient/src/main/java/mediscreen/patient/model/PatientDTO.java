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
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class PatientDTO {

    public final static String FAMILY_NOT_BLANK_ERROR = "Family name must contain at least one non-whitespace character";
    public final static String GIVEN_NOT_BLANK_ERROR = "Given name must contain at least one non-whitespace character";
    public final static String DOB_NOT_NULL_ERROR = "Date of birth may not be null";
    public final static String DOB_PAST_ERROR = "Date of birth must be a date in the past. Did you use format yyyy-mm-dd for JSON input ?";
    public final static String SEX_NOT_NULL_ERROR = "Sex may not be null";
    public final static String SEX_PATTERN_ERROR = "Sex must be M or F";
    public final static String ADDRESS_NOT_BLANK_ERROR = "Address must contain at least one non-whitespace character";
    public final static String PHONE_NOT_BLANK_ERROR = "Phone number must contain at least one non-whitespace character";
    public final static String PHONE_PATTERN_ERROR = "Phone number may only include plus sign, spaces, digits, brackets and dashes";

    long id;

    @NotBlank(message=FAMILY_NOT_BLANK_ERROR)
    String family;

    @NotBlank(message=GIVEN_NOT_BLANK_ERROR)
    String given;

    @NotNull(message=DOB_NOT_NULL_ERROR)
    @Past(message=DOB_PAST_ERROR)
    @JsonFormat(pattern="yyyy-MM-dd")
    Date dob;

    @NotNull(message=SEX_NOT_NULL_ERROR)
    @Pattern(regexp = "^[M|F]{1}$", message=SEX_PATTERN_ERROR)
    String sex;

    @NotBlank(message=ADDRESS_NOT_BLANK_ERROR)
    String address;

    @NotBlank(message=PHONE_NOT_BLANK_ERROR)
    @Pattern(regexp = ".*[- +()0-9]", message=PHONE_PATTERN_ERROR)
    String phone;

    public PatientDTO(PatientEntity patient) {
        this(patient.id, patient.family, patient.given, patient.dob, patient.sex, patient.address, patient.phone);
    }
}
