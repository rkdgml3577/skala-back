package com.sk.skala.shopapi.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private Long productId;
    private Integer quantity;
}