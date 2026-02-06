package com.minicommerce.backend.web.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
  private long id;
  private String name;
  private BigDecimal price;
  private int stock;
}


