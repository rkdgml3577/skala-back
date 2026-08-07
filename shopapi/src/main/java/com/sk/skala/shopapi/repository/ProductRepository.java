package com.sk.skala.shopapi.repository;

import com.sk.skala.shopapi.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품 이름으로 조회 (중복 확인용)
    Optional<Product> findByProductName(String productName);
}