package mediscreen.report.controller;

import mediscreen.report.service.DoctorUnavailableException;
import mediscreen.report.service.PatientNotFoundException;
import mediscreen.report.service.PatientNotUniqueException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionManager extends ResponseEntityExceptionHandler {

    public final static String EXCEPTION_MANAGER_REQUEST_PARAM_CONFLICT = "You must choose between requesting a report by patient id or by patient family name. Please check your request or ask your IT support.";
    public final static String EXCEPTION_MANAGER_REQUEST_PARAM_TYPE = "Your request has not the right format. Please check or ask your IT support.";
    public final static String EXCEPTION_MANAGER_PATIENT_NOT_FOUND = "No report has been found for this patient. Please check your request or ask your IT support.";
    public final static String EXCEPTION_MANAGER_PATIENT_NOT_UNIQUE = "Several patients match your report request. Please check or ask your IT support.";
    public final static String EXCEPTION_MANAGER_DOCTOR_UNAVAILABLE = "Notes are currently unavailable. Please ask your IT support.";

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

    @ExceptionHandler(value=RequestParamConflictException.class)
    public ResponseEntity<Object> handleRequestParamConflict(RequestParamConflictException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_REQUEST_PARAM_CONFLICT, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value= PatientNotFoundException.class)
    public ResponseEntity<Object> handlePatientNotFound(PatientNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value= DoctorUnavailableException.class)
    public ResponseEntity<Object> handleDoctorUnavailable(DoctorUnavailableException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_DOCTOR_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value= PatientNotUniqueException.class)
    public ResponseEntity<Object> handlePatientNotUnique(PatientNotUniqueException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_PATIENT_NOT_UNIQUE, HttpStatus.CONFLICT);
    }
}
