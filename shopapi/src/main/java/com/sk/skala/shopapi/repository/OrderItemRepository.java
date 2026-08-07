package com.sk.skala.shopapi.repository;

import com.sk.skala.shopapi.data.Customer;
import com.sk.skala.shopapi.data.OrderItem;
import com.sk.skala.shopapi.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 특정 고객의 주문 목록 조회 (customer의 customerId로)
    List<OrderItem> findByCustomer_CustomerId(String customerId);

    // 특정 고객이 특정 상품을 보유 중인지 조회
    Optional<OrderItem> findByCustomerAndProduct(Customer customer, Product product);
}