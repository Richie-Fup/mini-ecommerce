package com.minicommerce.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.minicommerce.backend.repository.InMemoryIdempotencyStore;
import com.minicommerce.backend.repository.InMemoryOrderRepository;
import com.minicommerce.backend.repository.InMemoryProductRepository;
import com.minicommerce.backend.web.error.InsufficientStockException;
import com.minicommerce.backend.web.error.IdempotencyKeyConflictException;
import com.minicommerce.backend.web.error.NotFoundException;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

  @Test
  void createOrder_shouldDecreaseStockAndReturnOrder() {
    var productRepo = new InMemoryProductRepository();
    var orderRepo = new InMemoryOrderRepository();
    var idempotency = new InMemoryIdempotencyStore();
    var service = new OrderService(productRepo, orderRepo, idempotency);

    var before = productRepo.findById(1L).orElseThrow().getStock();
    var order = service.createOrderIdempotent("k-1", 1L, 2);

    assertNotNull(order);
    assertEquals(1L, order.getProductId());
    assertEquals(2, order.getQuantity());
    assertEquals(1L, order.getId());

    var updatedProduct = productRepo.findById(1L).orElseThrow();
    assertEquals(before - 2, updatedProduct.getStock());
  }

  @Test
  void createOrder_shouldThrowWhenInsufficientStock() {
    var productRepo = new InMemoryProductRepository();
    var orderRepo = new InMemoryOrderRepository();
    var idempotency = new InMemoryIdempotencyStore();
    var service = new OrderService(productRepo, orderRepo, idempotency);

    assertThrows(InsufficientStockException.class, () -> service.createOrderIdempotent("k-2", 3L, 999));
  }

  @Test
  void createOrder_shouldThrowWhenProductNotFound() {
    var productRepo = new InMemoryProductRepository();
    var orderRepo = new InMemoryOrderRepository();
    var idempotency = new InMemoryIdempotencyStore();
    var service = new OrderService(productRepo, orderRepo, idempotency);

    assertThrows(NotFoundException.class, () -> service.createOrderIdempotent("k-3", 999L, 1));
  }

  @Test
  void createOrderIdempotent_shouldNotDoubleDecreaseStockForSameKey() {
    var productRepo = new InMemoryProductRepository();
    var orderRepo = new InMemoryOrderRepository();
    var idempotency = new InMemoryIdempotencyStore();
    var service = new OrderService(productRepo, orderRepo, idempotency);

    var before = productRepo.findById(2L).orElseThrow().getStock();

    var o1 = service.createOrderIdempotent("key-123", 2L, 1);
    var o2 = service.createOrderIdempotent("key-123", 2L, 1);

    assertEquals(o1.getId(), o2.getId());
    assertEquals(before - 1, productRepo.findById(2L).orElseThrow().getStock());
  }

  @Test
  void createOrderIdempotent_shouldConflictWhenSameKeyUsedForDifferentPayload() {
    var productRepo = new InMemoryProductRepository();
    var orderRepo = new InMemoryOrderRepository();
    var idempotency = new InMemoryIdempotencyStore();
    var service = new OrderService(productRepo, orderRepo, idempotency);

    service.createOrderIdempotent("key-abc", 2L, 1);

    assertThrows(IdempotencyKeyConflictException.class, () ->
        service.createOrderIdempotent("key-abc", 2L, 2));
  }
}


