package com.sk.skala.shopapi.config;

import com.sk.skala.shopapi.data.Customer;
import com.sk.skala.shopapi.data.Product;
import com.sk.skala.shopapi.repository.CustomerRepository;
import com.sk.skala.shopapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("초기 테스트 데이터 적재를 시작합니다...");

        // 1. 초기 상품 데이터 세팅 (Product 생성자: 이름, 가격)
        if (productRepository.count() == 0) {
            Product p1 = new Product("맥북 프로 16인치", 3500000.0);
            Product p2 = new Product("로지텍 무선 마우스", 120000.0);
            Product p3 = new Product("기계식 키보드", 150000.0);
            
            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);
            log.info("상품 데이터 세팅 완료");
        }

        // 2. 테스트용 고객 데이터 세팅 (Customer 생성자: ID, 포인트)
        if (customerRepository.count() == 0) {
            Customer testUser = new Customer("testuser", 5000000.0);
            
            // Setter를 사용하여 나머지 정보(이름, 비밀번호) 투명하게 주입
            testUser.setCustomerName("테스트고객");
            testUser.setCustomerPassword("1234"); 
            
            customerRepository.save(testUser);
            log.info("테스트 고객 데이터 세팅 완료");
        }
        
        log.info("모든 초기 데이터 적재가 완료되었습니다.");
    }
}