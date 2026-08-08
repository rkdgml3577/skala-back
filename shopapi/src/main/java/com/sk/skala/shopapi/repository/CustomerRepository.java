package com.sk.skala.shopapi.repository;

import com.sk.skala.shopapi.data.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByCustomerName(String customerName);

    @Modifying // 데이터 변경 쿼리임을 명시
    @Query("UPDATE Customer c SET c.customerPoint = c.customerPoint - :price WHERE c.customerId = :customerId AND c.customerPoint >= :price")
    int deductPoint(@Param("customerId") String customerId, @Param("price") double price);

    // ★ 추가: 비관적 쓰기 락(Pessimistic Write Lock)을 적용한 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Customer c WHERE c.customerId = :customerId")
    Optional<Customer> findByIdWithPessimisticLock(@Param("customerId") String customerId);
}