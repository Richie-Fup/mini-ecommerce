package com.minicommerce.backend.repository;

import com.minicommerce.backend.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
  List<Product> findAll();
  Optional<Product> findById(long id);
}


