package com.sk.skala.shopapi.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderListDto {
    private String customerId;
    private Double customerPoint;
    private List<OrderItemDto> products;
}