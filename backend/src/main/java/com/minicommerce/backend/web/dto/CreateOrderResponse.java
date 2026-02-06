package com.minicommerce.backend.web.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderResponse {
  private long orderId;
  private BigDecimal totalPrice;
}


