package com.sk.skala.shopapi.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    private String customerId;   // ★ ID가 문자열 (자동증가 아님, 직접 지정)

    private String customerPassword;
    private Double customerPoint;   // 보유 포인트

    // 고객ID·초기포인트를 받는 생성자
    public Customer(String customerId, Double customerPoint) {
        this.customerId = customerId;
        this.customerPoint = customerPoint;
    }
}