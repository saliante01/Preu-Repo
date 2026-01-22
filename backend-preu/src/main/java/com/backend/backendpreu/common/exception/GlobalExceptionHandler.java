package com.backend.backendpreu.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class provides centralized exception handling across all REST controllers,
 * converting various exceptions into standardized HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions specifically thrown when method arguments annotated with @Valid
     * fail validation. It collects all validation errors and returns them as a map
     * with field names as keys and error messages as values.
     *
     * @param ex The {@link MethodArgumentNotValidException} that occurred.
     * @return A {@link ResponseEntity} containing a map of validation errors and a {@code 400 Bad Request} status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles general {@link RuntimeException}s that occur during request processing.
     * This provides a fallback for unexpected errors or custom runtime exceptions
     * that are not specifically handled by other exception handlers.
     *
     * @param ex The {@link RuntimeException} that occurred.
     * @return A {@link ResponseEntity} containing an error message and a {@code 400 Bad Request} status.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}
