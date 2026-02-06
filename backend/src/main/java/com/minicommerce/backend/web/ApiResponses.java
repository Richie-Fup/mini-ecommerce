package com.minicommerce.backend.web;

import com.minicommerce.backend.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Small helper interface to reduce controller boilerplate for standard API envelopes.
 *
 * <p>Controllers can {@code implements ApiResponses} and then return {@code ok(data)} or
 * {@code created(data)}.
 */
public interface ApiResponses {

  default <T> ResponseEntity<ApiResponse<T>> ok(T data) {
    return ResponseEntity.ok(ApiResponse.ok(data));
  }

  default <T> ResponseEntity<ApiResponse<T>> created(T data) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data));
  }
}


