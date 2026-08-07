package com.sk.skala.shopapi.service;

import com.sk.skala.shopapi.data.*;
import com.sk.skala.shopapi.exception.Error;
import com.sk.skala.shopapi.exception.ResponseException;
import com.sk.skala.shopapi.repository.CustomerRepository;
import com.sk.skala.shopapi.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    // 전체 고객 조회 (페이징)
    public Response getAllCustomers(int offset, int count) {
        Pageable pageable = PageRequest.of(offset, count);
        Page<Customer> page = customerRepository.findAll(pageable);

        Response response = new Response();
        response.setSuccess(page.getContent());
        return response;
    }

    // 단일 고객 + 주문 목록 조회
    @Transactional(readOnly = true)
    public Response getCustomerById(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));

        // 이 고객의 주문 목록 조회
        List<OrderItem> orderItems = orderItemRepository.findByCustomer_CustomerId(customerId);

        // OrderItem → OrderItemDto 변환
        List<OrderItemDto> products = orderItems.stream()
                .map(item -> OrderItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getProductName())
                        .productPrice(item.getProduct().getProductPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // 고객 + 주문목록을 OrderListDto로 묶기
        OrderListDto dto = OrderListDto.builder()
                .customerId(customer.getCustomerId())
                .customerPoint(customer.getCustomerPoint())
                .products(products)
                .build();

        Response response = new Response();
        response.setSuccess(dto);
        return response;
    }

    // 고객 생성 (회원가입)
    public Response createCustomer(Customer customer) {
        // 입력값 검증
        if (customer.getCustomerId() == null || customer.getCustomerId().isEmpty()
                || customer.getCustomerPassword() == null || customer.getCustomerPassword().isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND, "ID와 비밀번호는 필수입니다");
        }
        // 중복 ID 체크
        if (customerRepository.findById(customer.getCustomerId()).isPresent()) {
            throw new ResponseException(Error.DATA_DUPLICATED);
        }
        // 초기 포인트 지급 (없으면 100만 포인트)
        if (customer.getCustomerPoint() == null) {
            customer.setCustomerPoint(1000000.0);
        }
        Customer saved = customerRepository.save(customer);

        Response response = new Response();
        response.setSuccess(saved);
        return response;
    }
}