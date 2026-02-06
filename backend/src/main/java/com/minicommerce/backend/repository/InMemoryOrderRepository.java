package com.minicommerce.backend.repository;

import com.minicommerce.backend.domain.Order;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {
  private final Map<Long, Order> orders = new ConcurrentHashMap<>();
  private final AtomicLong idSeq = new AtomicLong(0);

  @Override
  public Order save(Order order) {
    long id = idSeq.incrementAndGet();
    var stored = new Order(
        id,
        order.getProductId(),
        order.getQuantity(),
        order.getUnitPrice(),
        order.getTotalPrice(),
        order.getCreatedAt()
    );
    orders.put(id, stored);
    return stored;
  }

  @Override
  public Optional<Order> findById(long id) {
    return Optional.ofNullable(orders.get(id));
  }
}


