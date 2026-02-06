package com.minicommerce.backend.web.error;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
    log.warn("Resource not found: {} - {}", req.getRequestURI(), ex.getMessage());
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    enrich(pd, req);
    return pd;
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ProblemDetail handleInsufficientStock(InsufficientStockException ex, HttpServletRequest req) {
    log.warn("Insufficient stock: {} - {}", req.getRequestURI(), ex.getMessage());
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    enrich(pd, req);
    return pd;
  }

  @ExceptionHandler(IdempotencyKeyConflictException.class)
  public ProblemDetail handleIdempotencyConflict(IdempotencyKeyConflictException ex, HttpServletRequest req) {
    log.warn("Idempotency key conflict: {} - {}", req.getRequestURI(), ex.getMessage());
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    enrich(pd, req);
    return pd;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .reduce((a, b) -> a + ", " + b)
        .orElse("Unknown validation error");
    log.warn("Validation failed: {} - Errors: {}", req.getRequestURI(), errors);
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    pd.setProperty(
        "errors",
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of(
                "field", fe.getField(),
                "message", fe.getDefaultMessage()
            ))
            .toList()
    );
    enrich(pd, req);
    return pd;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
    log.warn("Illegal argument: {} - {}", req.getRequestURI(), ex.getMessage());
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    enrich(pd, req);
    return pd;
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ProblemDetail handleSpringErrorResponse(ErrorResponseException ex, HttpServletRequest req) {
    log.warn("Spring error response: {} - Status: {} - {}", 
        req.getRequestURI(), 
        ex.getStatusCode(), 
        ex.getMessage());
    var pd = ex.getBody();
    enrich(pd, req);
    return pd;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest req) {
    // Log full exception with stack trace for unexpected errors
    log.error("Unexpected error occurred: {} - {}", req.getRequestURI(), ex.getMessage(), ex);
    var pd = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred");
    enrich(pd, req);
    return pd;
  }

  private static void enrich(ProblemDetail pd, HttpServletRequest req) {
    pd.setProperty("timestamp", Instant.now().toString());
    pd.setProperty("path", req.getRequestURI());
  }
}


