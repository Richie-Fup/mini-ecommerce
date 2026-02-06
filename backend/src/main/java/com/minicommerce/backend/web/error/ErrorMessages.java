package com.minicommerce.backend.web.error;

/**
 * Centralized error messages to avoid duplication and keep wording consistent.
 */
public final class ErrorMessages {
  private ErrorMessages() {}

  public static final String IDEMPOTENCY_KEY_REQUIRED = "Idempotency-Key header is required";
  public static final String IDEMPOTENCY_KEY_BLANK = "Idempotency-Key must not be blank";
  public static final String IDEMPOTENCY_KEY_REUSED_DIFFERENT_PAYLOAD =
      "Idempotency-Key was already used with a different payload";
}


