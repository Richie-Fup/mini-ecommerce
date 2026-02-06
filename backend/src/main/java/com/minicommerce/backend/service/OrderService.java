package com.minicommerce.backend.service;

import com.minicommerce.backend.domain.Order;
import com.minicommerce.backend.domain.Product;
import com.minicommerce.backend.repository.IdempotencyStore;
import com.minicommerce.backend.repository.OrderRepository;
import com.minicommerce.backend.repository.ProductRepository;
import com.minicommerce.backend.web.error.ErrorMessages;
import com.minicommerce.backend.web.error.InsufficientStockException;
import com.minicommerce.backend.web.error.NotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final IdempotencyStore idempotencyStore;

  /**
   * Create an order with idempotency. Caller must provide a non-blank idempotency key.
   *
   * <p>In dev and production, clients should always provide this header to prevent duplicate
   * orders due to retries/double-clicks.
   */
  public Order createOrderIdempotent(String idempotencyKey, long productId, int quantity) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new IllegalArgumentException(ErrorMessages.IDEMPOTENCY_KEY_REQUIRED);
    }
    return idempotencyStore.getOrCreate(
        idempotencyKey,
        productId,
        quantity,
        () -> createOrder(productId, quantity)
    );
  }

  private Order createOrder(long productId, int quantity) {
    if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

    Product product = productRepository
        .findById(productId)
        .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

    boolean ok = product.tryDecreaseStock(quantity);
    if (!ok) {
      throw new InsufficientStockException(
          "Insufficient stock for product " + productId + ", requested " + quantity);
    }

    BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    var order = new Order(
        0L,
        product.getId(),
        quantity,
        product.getPrice(),
        total,
        Instant.now()
    );

    return orderRepository.save(order);
  }

  public Order getOrder(long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
  }
}


