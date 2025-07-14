package com.undoschool.coursesearch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("Type mismatch error", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid parameter type");
        response.put("message", "Parameter '" + e.getName() + "' has invalid value: " + e.getValue());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParseException(DateTimeParseException e) {
        log.error("Date time parsing error", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid date format");
        response.put("message", "Please use ISO-8601 format: yyyy-MM-ddTHH:mm:ss");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
