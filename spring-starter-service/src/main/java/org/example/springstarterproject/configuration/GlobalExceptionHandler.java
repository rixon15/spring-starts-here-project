package org.example.springstarterproject.configuration;

import com.example.models.Error;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Error errorResponse = new Error();
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Validation Failed: " + errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleJsonErrors(HttpMessageNotReadableException ex) {

        Error errorResponse = new Error();
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Invalid Request Body: The JSON format or data types are incorrect.");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handleEntityNotFound(EntityNotFoundException ex) {

        Error errorResponse = new Error();
        errorResponse.setCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralExceptions(Exception ex) {

        Error errorResponse = new Error();
        errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage("An unexpected error occurred.");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
