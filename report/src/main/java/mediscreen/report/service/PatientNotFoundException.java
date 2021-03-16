package mediscreen.report.service;

public class PatientNotFoundException extends Throwable {

    PatientNotFoundException(String message) {
        super(message);
    }
}
