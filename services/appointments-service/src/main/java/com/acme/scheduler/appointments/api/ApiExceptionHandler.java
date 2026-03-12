package com.acme.scheduler.appointments.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    int code = ex.getMessage() != null && ex.getMessage().startsWith("NOT_FOUND") ? 404 : 400;
    return ResponseEntity.status(code).body(Map.of(
        "error", code == 404 ? "Not Found" : "Bad Request",
        "message", ex.getMessage(),
        "timestamp", Instant.now().toString(),
        "status", code
    ));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> conflictOrBadRequest(IllegalStateException ex) {
    int code = 400;
    if (ex.getMessage() != null && ex.getMessage().startsWith("IDEMPOTENCY_CONFLICT")) code = 409;
    if (ex.getMessage() != null && ex.getMessage().startsWith("SLOT_TAKEN")) code = 409;
    if (ex.getMessage() != null && ex.getMessage().startsWith("INVALID_STATE")) code = 409;

    return ResponseEntity.status(code).body(Map.of(
        "error", code == 409 ? "Conflict" : "Bad Request",
        "message", ex.getMessage(),
        "timestamp", Instant.now().toString(),
        "status", code
    ));
  }
}
