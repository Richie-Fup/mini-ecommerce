package com.minicommerce.backend.web.mapper;

import com.minicommerce.backend.domain.Product;
import com.minicommerce.backend.web.dto.ProductResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "stock", expression = "java(product.getStock())")
  ProductResponse toResponse(Product product);

  List<ProductResponse> toResponseList(List<Product> products);
}


