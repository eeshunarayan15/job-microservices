package com.micro.company.exception;

import com.micro.company.respone.Apiresponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Apiresponse<Object>> handleServiceException(ServiceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Apiresponse<>("Error", e.getMessage(), null));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Apiresponse<Object>> handleDataAccessException(DataAccessException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Apiresponse<>("Error", "Database operation failed", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Apiresponse<Object>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Apiresponse<>("Error", "An unexpected error occurred", null));
    }
    @ExceptionHandler(DublicateResourceException.class)
    public ResponseEntity<Apiresponse<Object>> handleDublicateResourceException(DublicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Apiresponse<>("Error", e.getMessage(), null));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Apiresponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Apiresponse<>("Error", e.getMessage(), null));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Apiresponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Create a response with validation errors
        Apiresponse<Object> response = new Apiresponse<>(
                "Validation Error",
                "Request validation failed",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
