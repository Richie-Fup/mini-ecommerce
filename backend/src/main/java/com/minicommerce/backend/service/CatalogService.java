package com.minicommerce.backend.service;

import com.minicommerce.backend.domain.Product;
import com.minicommerce.backend.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogService {
  private final ProductRepository productRepository;

  @Cacheable(value = "products", key = "'all'")
  public List<Product> listProducts() {
    return productRepository.findAll();
  }
}


