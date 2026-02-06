package com.minicommerce.backend.web.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private ApiStatus status;
  private T data;
  private String timestamp;

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(ApiStatus.OK, data, Instant.now().toString());
  }

  public static <T> ApiResponse<T> created(T data) {
    return new ApiResponse<>(ApiStatus.CREATED, data, Instant.now().toString());
  }
}


