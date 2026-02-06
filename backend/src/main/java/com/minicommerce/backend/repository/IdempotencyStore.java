package com.minicommerce.backend.repository;

import com.minicommerce.backend.domain.Order;
import java.util.function.Supplier;

/**
 * Simple in-memory idempotency store to prevent duplicate order creation on retries/double-clicks.
 *
 * <p>Keyed by a client-supplied Idempotency-Key (string).
 */
public interface IdempotencyStore {
  Order getOrCreate(String key, long productId, int quantity, Supplier<Order> creator);
}


