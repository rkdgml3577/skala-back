package com.sk.skala.shopapi.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "product_id"}) // 2줄 생성 원천 차단
    })

@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)   // 여러 주문이 한 고객을 참조
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)   // 여러 주문이 한 상품을 참조
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;   // 주문 수량

    // 고객·상품·수량을 받는 생성자
    public OrderItem(Customer customer, Product product, Integer quantity) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
    }
}