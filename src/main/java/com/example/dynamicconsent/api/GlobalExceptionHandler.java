package com.example.dynamicconsent.api;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                ex.getMessage(),
                null,
                generateRequestId()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_FAILED",
                "Authentication failed.",
                null,
                generateRequestId()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                "Access denied.",
                null,
                generateRequestId()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                ex.getDetails(),
                generateRequestId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleBusinessException(BusinessException ex) {
        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                ex.getStatus().value(),
                ex.getCode(),
                ex.getMessage(),
                ex.getDetails(),
                generateRequestId()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.put(error.getField(), error.getDefaultMessage());
        });

        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Validation failed.",
                details,
                generateRequestId()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonDTOs.ErrorResponse> handleGeneric(Exception ex) {
        CommonDTOs.ErrorResponse error = new CommonDTOs.ErrorResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "An unexpected error occurred.",
                null,
                generateRequestId()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    // Custom exceptions
    public static class ResourceNotFoundException extends RuntimeException {
        private final Map<String, Object> details;

        public ResourceNotFoundException(String message, Map<String, Object> details) {
            super(message);
            this.details = details;
        }

        public Map<String, Object> getDetails() {
            return details;
        }
    }

    public static class BusinessException extends RuntimeException {
        private final HttpStatus status;
        private final String code;
        private final Map<String, Object> details;

        public BusinessException(String code, String message, HttpStatus status, Map<String, Object> details) {
            super(message);
            this.code = code;
            this.status = status;
            this.details = details;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }

        public Map<String, Object> getDetails() {
            return details;
        }
    }
}