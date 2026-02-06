package com.minicommerce.backend.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {
  private long id;
  private long productId;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
  private Instant createdAt;
}


