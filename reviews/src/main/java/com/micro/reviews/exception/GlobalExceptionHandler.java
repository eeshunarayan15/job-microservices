package com.micro.reviews.exception;


import com.micro.reviews.response.Apiresponse;
import feign.FeignException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;

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

//    @ExceptionHandler(FeignException.class)
//    public ResponseEntity<Apiresponse<Object>> handleFeignException(FeignException e) {
//        return ResponseEntity.status(e.status())
//                .body(new Apiresponse<>("Error", "External service error: " + e.getMessage(), null));
//    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Apiresponse<Object>> handleFeignException(FeignException e) {
        // Check if status is valid (between 100-599), otherwise use 503
        int status = e.status();
        HttpStatus httpStatus;

        if (status >= 100 && status < 600) {
            httpStatus = HttpStatus.valueOf(status);
        } else {
            // Invalid status code (like -1 for timeout), use SERVICE_UNAVAILABLE
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
        }

        return ResponseEntity.status(httpStatus)
                .body(new Apiresponse<>("Error", "External service error: " + e.getMessage(), null));
    }
    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Apiresponse<Object>> handleSocketTimeoutException(SocketTimeoutException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new Apiresponse<>("Error", "Connection timeout. The service is taking too long to respond.", null));
    }
    @ExceptionHandler(feign.RetryableException.class)
    public ResponseEntity<Apiresponse<Object>> handleRetryableException(feign.RetryableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new Apiresponse<>("Error", "Service temporarily unavailable. Please try again later.", null));
    }
}
