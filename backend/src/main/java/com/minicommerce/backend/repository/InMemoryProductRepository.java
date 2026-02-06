package com.minicommerce.backend.repository;

import com.minicommerce.backend.domain.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryProductRepository implements ProductRepository {
  private final Map<Long, Product> products = new ConcurrentHashMap<>();

  public InMemoryProductRepository() {
    // Seed some sample products
    products.put(1L, new Product(1L, "Renewal Night Serum (50ml)", new BigDecimal("115.00"), 12));
    products.put(2L, new Product(2L, "Longwear Foundation (30ml)", new BigDecimal("52.00"), 9));
    products.put(3L, new Product(3L, "Firming Moisturizer (50ml)", new BigDecimal("105.00"), 7));
    products.put(4L, new Product(4L, "Gentle Foaming Cleanser", new BigDecimal("29.00"), 10));
    products.put(5L, new Product(5L, "Sculpting Lipstick", new BigDecimal("34.00"), 6));
  }

  @Override
  public List<Product> findAll() {
    var list = new ArrayList<>(products.values());
    list.sort(Comparator.comparingLong(Product::getId));
    return List.copyOf(list);
  }

  @Override
  public Optional<Product> findById(long id) {
    return Optional.ofNullable(products.get(id));
  }
}


