package com.minicommerce.backend.web.error;

public class IdempotencyKeyConflictException extends RuntimeException {
  public IdempotencyKeyConflictException(String message) {
    super(message);
  }
}


