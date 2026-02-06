package com.minicommerce.backend.web.mapper;

import com.minicommerce.backend.domain.Order;
import com.minicommerce.backend.web.dto.CreateOrderResponse;
import com.minicommerce.backend.web.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  OrderResponse toResponse(Order order);

  @Mapping(source = "id", target = "orderId")
  CreateOrderResponse toCreateResponse(Order order);
}


