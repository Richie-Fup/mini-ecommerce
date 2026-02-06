package com.minicommerce.backend.repository;

import com.minicommerce.backend.domain.Order;
import java.util.Optional;

public interface OrderRepository {
  Order save(Order order);
  Optional<Order> findById(long id);
}


