package com.minicommerce.backend.domain;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public final class Product {
  private final long id;
  private final String name;
  private final BigDecimal price;
  private final AtomicInteger stock;

  public Product(long id, String name, BigDecimal price, int stock) {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
    if (price == null || price.signum() < 0) throw new IllegalArgumentException("price must be >= 0");
    if (stock < 0) throw new IllegalArgumentException("stock must be >= 0");
    this.id = id;
    this.name = name;
    this.price = price;
    this.stock = new AtomicInteger(stock);
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getStock() {
    return stock.get();
  }

  /**
   * Atomically decreases stock if current stock is enough.
   *
   * @return true if the stock was decreased; false otherwise
   */
  public boolean tryDecreaseStock(int quantity) {
    if (quantity <= 0) return false;
    while (true) {
      int current = stock.get();
      if (current < quantity) return false;
      if (stock.compareAndSet(current, current - quantity)) return true;
    }
  }
}


