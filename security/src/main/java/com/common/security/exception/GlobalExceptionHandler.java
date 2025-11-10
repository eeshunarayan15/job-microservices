package com.common.security.exception;

import com.common.security.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ========================================
    // SPECIFIC EXCEPTIONS FIRST (Most important!)
    // ========================================

    // 🔹 Handle HTTP Method Not Allowed (405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {

        log.error("Method not allowed: {}", ex.getMessage());

        String message = String.format(
                "HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(),
                ex.getSupportedHttpMethods()
        );

        ApiResponse<Object> response = new ApiResponse<>("error", message, null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    // 🔹 Handle Access Denied (403) - Spring Security
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                "Access denied. You don't have permission to access this resource.",
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 🔹 Handle invalid login (401) - Spring Security
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>("error", "Invalid email or password", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 🔹 Handle validation errors (400) - @Valid failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Invalid input");

        log.error("Validation error: {}", message);

        ApiResponse<Object> response = new ApiResponse<>("error", message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 🔹 Handle constraint violations (400) - @Email, @NotBlank
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 🔹 Handle IllegalArgumentException (400) - Your custom business logic errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========================================
    // GENERIC HANDLERS LAST
    // ========================================

    // 🔹 ⚠️ REMOVE THIS or make it more specific
    // RuntimeException is TOO BROAD and catches Spring exceptions incorrectly
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex) {
//        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }

    // 🔹 Catch-all for unexpected exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        // ⚠️ IMPORTANT: Log the actual exception for debugging!
        log.error("Unhandled exception - Type: {}, Message: {}",
                ex.getClass().getName(),
                ex.getMessage(),
                ex  // This logs the full stack trace
        );

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                "Internal server error: " + ex.getClass().getSimpleName(),  // Show exception type in dev
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    // 🔹 Handle user already exists (409 Conflict)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}