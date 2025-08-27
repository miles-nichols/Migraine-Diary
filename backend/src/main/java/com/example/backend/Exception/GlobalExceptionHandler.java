package com.example.backend.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateDate(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "You've already logged an episode for this date.");
        return ResponseEntity.badRequest().body(response);
    }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Something went wrong. Please try again.");
        return ResponseEntity.internalServerError().body(response);
    }
}