package mediscreen.doctor.model;

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

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PUBLIC)
public class NoteDTO {

    public final static String FAMILY_NOT_BLANK_ERROR = "You must enter a family name for your patient. Please check your request and try again.";
    public final static String GIVEN_NOT_BLANK_ERROR = "You must enter a given name for your patient. Please check your request and try again.";
    public final static String DOB_NOT_NULL_ERROR = "An unexpected error happened with the date of birth you registered. Please contact your IT support.";
    public final static String DOB_PAST_ERROR = "A date of birth must be in the past. Please check your request and try again.";
    public final static String SEX_NOT_NULL_ERROR = "You must enter a sex for your patient. Please check your request and try again.";
    public final static String SEX_PATTERN_ERROR = "Your patient may only have sex M or F. Please check your request and try again.";
    public final static String ADDRESS_NOT_BLANK_ERROR = "You must enter an address for your patient. Please check your request and try again.";
    public final static String PHONE_NOT_BLANK_ERROR = "You must enter a phone number for your patient. Please check your request and try again.";
    public final static String PHONE_PATTERN_ERROR = "You entered an invalid phone number for your patient. The following characters are allowed : plus sign, spaces, digits, brackets and dashes.  Please check your request and try again.";

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

    public NoteDTO(NoteEntity patient) {
        this(patient.id, patient.family, patient.given, patient.dob, patient.sex.toUpperCase(), patient.address, patient.phone);
    }
}
