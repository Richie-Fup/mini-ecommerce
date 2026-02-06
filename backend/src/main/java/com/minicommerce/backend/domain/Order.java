package com.minicommerce.backend.domain;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
  private long id;
  private long productId;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
  private Instant createdAt;
}


