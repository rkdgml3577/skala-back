package com.sk.skala.shopapi.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
}