package mediscreen.doctor.controller;

import mediscreen.doctor.service.CreateExistingNoteException;
import mediscreen.doctor.service.NoteNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionManager extends ResponseEntityExceptionHandler {

    public final static String EXCEPTION_MANAGER_NOTE_NOT_FOUND = "No such note has been found. Please check your request or ask your IT support.";
    public final static String EXCEPTION_MANAGER_CREATE_EXISTING_NOTE = "it seems that the note you wan't to save already exists in the database. Please check your request or ask your IT support.";

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value=CreateExistingNoteException.class)
    public ResponseEntity<Object> handleCreateExistingPatient(CreateExistingNoteException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_CREATE_EXISTING_NOTE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value= NoteNotFoundException.class)
    public ResponseEntity<Object> handleCreateExistingPatient(NoteNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(EXCEPTION_MANAGER_NOTE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
