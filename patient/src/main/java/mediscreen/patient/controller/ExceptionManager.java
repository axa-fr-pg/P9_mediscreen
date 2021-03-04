package mediscreen.patient.controller;

import mediscreen.patient.service.CreateExistingPatientException;
import mediscreen.patient.service.PatientNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    public final static String EXCEPTION_MANAGER_PATIENT_NOT_FOUND = "No such patient has been found. Please check your request or ask your IT support.";
    public final static String EXCEPTION_MANAGER_CREATE_EXISTING_PATIENT = "it seems that the patient you wan't to save already exists in the database. Please check your request or ask your IT support.";
    public final static String EXCEPTION_MANAGER_REQUEST_PARAM_TYPE = "Your request has not the right format. Please check your request or ask your IT support.";

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_REQUEST_PARAM_TYPE, HttpStatus.BAD_REQUEST);
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
