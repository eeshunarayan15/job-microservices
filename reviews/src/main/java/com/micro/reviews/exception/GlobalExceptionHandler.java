package com.micro.reviews.exception;


import com.micro.reviews.response.Apiresponse;
import feign.FeignException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Apiresponse<Object>> handleFeignNotFound(FeignException.NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Apiresponse<>("Error", "Company not found in external service", null));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Apiresponse<Object>> handleFeignException(FeignException e) {
        return ResponseEntity.status(e.status())
                .body(new Apiresponse<>("Error", "External service error: " + e.getMessage(), null));
    }
}
