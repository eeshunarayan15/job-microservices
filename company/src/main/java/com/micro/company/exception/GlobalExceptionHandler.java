package com.micro.company.exception;

import com.micro.company.respone.Apiresponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Apiresponse<Object>> handleServiceException(ServiceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Apiresponse<>("Error", e.getMessage(), null));
    }

//    @ExceptionHandler(DataAccessException.class)
//    public ResponseEntity<Apiresponse<Object>> handleDataAccessException(DataAccessException e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new Apiresponse<>("Error", "Database operation failed", null));
//    }

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

    // Handle Feign exceptions (e.g., when another microservice is down)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Apiresponse<Object>> handleFeignException(FeignException e) {
        String errorMessage = "Failed to communicate with another service: " + e.contentUTF8();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new Apiresponse<>("Error", errorMessage, null));
    }

    // Handle unknown host or connection issues
    @ExceptionHandler({java.net.ConnectException.class, java.net.UnknownHostException.class})
    public ResponseEntity<Apiresponse<Object>> handleConnectionExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new Apiresponse<>("Error", "Service temporarily unavailable: " + e.getMessage(), null));
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Apiresponse<Object>> handleDataAccessException(DataAccessException e) {
        log.error("Database operation failed with exception: ", e); // ADD THIS!
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Apiresponse<>("Error", "Database operation failed: " + e.getMessage(), null));
    }
}
