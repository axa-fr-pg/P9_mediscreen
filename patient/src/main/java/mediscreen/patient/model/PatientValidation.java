package mediscreen.patient.model;

public class PatientValidation {
    public final static String FAMILY_NOT_BLANK_ERROR = "You must enter a family name for your patient. Please check your request and try again.";
    public final static String GIVEN_NOT_BLANK_ERROR = "You must enter a given name for your patient. Please check your request and try again.";
    public final static String DOB_NOT_NULL_ERROR = "An unexpected error happened with the date of birth you registered. Please contact your IT support.";
    public final static String DOB_PAST_ERROR = "A date of birth must be in the past. Please check your request and try again.";
    public final static String SEX_NOT_NULL_ERROR = "You must enter a sex for your patient. Please check your request and try again.";
    public final static String SEX_PATTERN_ERROR = "Your patient may only have sex M or F. Please check your request and try again.";
    public final static String ADDRESS_NOT_BLANK_ERROR = "You must enter an address for your patient. Please check your request and try again.";
    public final static String PHONE_NOT_BLANK_ERROR = "You must enter a phone number for your patient. Please check your request and try again.";
    public final static String PHONE_PATTERN_ERROR = "You entered an invalid phone number for your patient. The following characters are allowed : plus sign, spaces, digits, brackets and dashes.  Please check your request and try again.";
}
