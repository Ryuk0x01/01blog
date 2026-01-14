package com.blog.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(Map.of(
                "status", "error",
                "message", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Runtime / custom exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(Map.of(
                "status", "error",
                "message", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // AccessDeniedException
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(Map.of(
                "status", "error",
                "message", "You are not authorized"), HttpStatus.FORBIDDEN);
    }

}
