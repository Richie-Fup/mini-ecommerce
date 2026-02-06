package com.minicommerce.backend.web.controller;

import com.minicommerce.backend.domain.Order;
import com.minicommerce.backend.service.OrderService;
import com.minicommerce.backend.web.dto.CreateOrderRequest;
import com.minicommerce.backend.web.dto.CreateOrderResponse;
import com.minicommerce.backend.web.dto.OrderResponse;
import com.minicommerce.backend.web.ApiHeaders;
import com.minicommerce.backend.web.mapper.OrderMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @PostMapping("/orders")
  public ResponseEntity<CreateOrderResponse> createOrder(
      @Valid @RequestBody CreateOrderRequest req,
      @RequestHeader(ApiHeaders.IDEMPOTENCY_KEY) String idempotencyKey
  ) {
    Order order = orderService.createOrderIdempotent(idempotencyKey, req.getProductId(), req.getQuantity());
    return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toCreateResponse(order));
  }

  @GetMapping("/orders/{id}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") long id) {
    Order order = orderService.getOrder(id);
    return ResponseEntity.ok(orderMapper.toResponse(order));
  }
}


