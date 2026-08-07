package com.sk.skala.shopapi.repository;

import com.sk.skala.shopapi.data.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // 사용자 정의 메서드 필요 없음 (기본 제공으로 충분)
    Optional<Customer> findByCustomerName(String customerName);
}
