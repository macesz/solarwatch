package com.codecool.solarwatch.customErrorHandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(InvalidLocationException.class)
  public ResponseEntity<String> handleInvalidLocationException(InvalidLocationException e) {
    logger.error("Invalid location: {}", e.getMessage());
    return ResponseEntity.badRequest().body("Location not valid");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    logger.error("Validation error: {}", e.getMessage());
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  // Handle missing required parameters
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException e) {
    logger.error("Missing parameter: {}", e.getMessage());
    return ResponseEntity.badRequest().body("Missing required parameter: " + e.getParameterName());
  }

  // Handle invalid parameter types (like invalid date format)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    logger.error("Parameter type mismatch: {}", e.getMessage());
    return ResponseEntity.badRequest().body("Invalid parameter format: " + e.getName());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    logger.error("Runtime error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Internal server error occurred");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception e) {
    logger.error("Unexpected error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
  }
}
