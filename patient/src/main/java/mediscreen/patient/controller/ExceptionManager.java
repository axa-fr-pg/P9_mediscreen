package mediscreen.patient.controller;

import mediscreen.patient.service.CreateExistingPatientException;
import mediscreen.patient.service.PatientNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionManager extends ResponseEntityExceptionHandler {

    public final static String EXCEPTION_MANAGER_PATIENT_NOT_FOUND = "Patient has not been found";
    public final static String EXCEPTION_MANAGER_CREATE_EXISTING_PATIENT = "Patient cannot be created with id=0 or with an already existing family/dob value";

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value=PatientNotFoundException.class)
    public ResponseEntity<Object> handlePatientNotFound(PatientNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value=CreateExistingPatientException.class)
    public ResponseEntity<Object> handleCreateExistingPatient(CreateExistingPatientException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_CREATE_EXISTING_PATIENT, HttpStatus.CONFLICT);
    }
}
