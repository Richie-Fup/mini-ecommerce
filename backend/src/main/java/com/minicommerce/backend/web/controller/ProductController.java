package com.minicommerce.backend.web.controller;

import com.minicommerce.backend.service.CatalogService;
import com.minicommerce.backend.web.dto.ProductResponse;
import com.minicommerce.backend.web.mapper.ProductMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
  private final CatalogService catalogService;
  private final ProductMapper productMapper;

  @GetMapping("/products")
  public ResponseEntity<List<ProductResponse>> listProducts() {
    return ResponseEntity.ok(productMapper.toResponseList(catalogService.listProducts()));
  }
}


