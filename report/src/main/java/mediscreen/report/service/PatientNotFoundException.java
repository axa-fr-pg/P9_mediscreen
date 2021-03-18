package mediscreen.report.service;

public class PatientNotFoundException extends Exception {

    PatientNotFoundException(String message) {
        super(message);
    }
}
