package com.minicommerce.backend.repository;

import com.minicommerce.backend.domain.Order;
import com.minicommerce.backend.web.constants.ErrorMessages;
import com.minicommerce.backend.web.error.IdempotencyKeyConflictException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryIdempotencyStore implements IdempotencyStore {

  private record Entry(long productId, int quantity, Order order) {}

  private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

  @Override
  public Order getOrCreate(String key, long productId, int quantity, Supplier<Order> creator) {
    Objects.requireNonNull(key, "key must not be null");
    if (key.isBlank()) throw new IllegalArgumentException(ErrorMessages.IDEMPOTENCY_KEY_BLANK);

    Entry entry = entries.compute(key, (k, existing) -> {
      if (existing != null) {
        if (existing.productId() != productId || existing.quantity() != quantity) {
          throw new IdempotencyKeyConflictException(
              ErrorMessages.IDEMPOTENCY_KEY_REUSED_DIFFERENT_PAYLOAD);
        }
        return existing;
      }
      Order order = creator.get();
      return new Entry(productId, quantity, order);
    });
    return entry.order();
  }
}


