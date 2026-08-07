package com.sk.skala.shopapi.service;

import com.sk.skala.shopapi.data.*;
import com.sk.skala.shopapi.exception.Error;
import com.sk.skala.shopapi.exception.ResponseException;
import com.sk.skala.shopapi.repository.CustomerRepository;
import com.sk.skala.shopapi.repository.OrderItemRepository;
import com.sk.skala.shopapi.repository.ProductRepository;
import com.sk.skala.shopapi.tools.SessionHandler;

import jakarta.servlet.http.HttpServletResponse;
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
    private final ProductRepository productRepository;  
    private final SessionHandler sessionHandler;


    // 전체 고객 조회 (페이징)
    public Response getAllCustomers(int offset, int count) {
        Pageable pageable = PageRequest.of(offset, count);
        Page<Customer> page = customerRepository.findAll(pageable);

        Response response = new Response();
        response.setSuccess(page.getContent());
        return response;
    }

    // 로그인
    public Response loginCustomer(Customer loginRequest, HttpServletResponse httpResponse) {
        // 입력값 검증
        if (loginRequest.getCustomerId() == null || loginRequest.getCustomerPassword() == null) {
            throw new ResponseException(Error.NOT_AUTHENTICATED, "ID와 비밀번호를 입력하세요");
        }
        // 고객 조회
        Customer customer = customerRepository.findById(loginRequest.getCustomerId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));
        // 비밀번호 검증
        if (!customer.getCustomerPassword().equals(loginRequest.getCustomerPassword())) {
            throw new ResponseException(Error.NOT_AUTHENTICATED, "비밀번호가 틀렸습니다");
        }
        // 인증 성공 → 토큰 발급 (쿠키에 담김)
        sessionHandler.storeAccessToken(httpResponse, customer.getCustomerId());

        // 비밀번호는 응답에서 제거
        customer.setCustomerPassword(null);
        Response response = new Response();
        response.setSuccess(customer);
        return response;
    }

    // 주문
    @Transactional
    public Response placeOrder(String customerId, OrderRequest request) {
        // 1. 고객 조회 (JWT로 받은 customerId)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));

        // 2. 상품 조회
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "상품을 찾을 수 없습니다"));

        // 3. 총액 계산
        double totalPrice = product.getProductPrice() * request.getQuantity();

        // 4. 포인트 검증
        if (customer.getCustomerPoint() < totalPrice) {
            throw new ResponseException(Error.INSUFFICIENT_FUNDS);
        }

        // 5. 포인트 차감
        customer.setCustomerPoint(customer.getCustomerPoint() - totalPrice);

        // 6. OrderItem 갱신 (있으면 수량 누적, 없으면 생성)
        orderItemRepository.findByCustomerAndProduct(customer, product)
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + request.getQuantity()),
                        () -> orderItemRepository.save(
                                new OrderItem(customer, product, request.getQuantity()))
                );

        Response response = new Response();
        response.setSuccess("주문 완료. 남은 포인트: " + customer.getCustomerPoint());
        return response;
    }

    // 취소
    @Transactional
    public Response cancelOrder(String customerId, OrderRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "상품을 찾을 수 없습니다"));

        // 주문 내역 확인
        OrderItem orderItem = orderItemRepository.findByCustomerAndProduct(customer, product)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "주문 내역이 없습니다"));

        // 취소 수량 검증
        if (orderItem.getQuantity() < request.getQuantity()) {
            throw new ResponseException(Error.DATA_NOT_FOUND, "취소 수량이 주문 수량보다 많습니다");
        }

        // 포인트 환급
        double refund = product.getProductPrice() * request.getQuantity();
        customer.setCustomerPoint(customer.getCustomerPoint() + refund);

        // 수량 차감 (0이면 삭제)
        int remaining = orderItem.getQuantity() - request.getQuantity();
        if (remaining == 0) {
            orderItemRepository.delete(orderItem);
        } else {
            orderItem.setQuantity(remaining);
        }

        Response response = new Response();
        response.setSuccess("취소 완료. 남은 포인트: " + customer.getCustomerPoint());
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

    public Response getCustomerByName(String customerName) {
        Customer customer = customerRepository.findByCustomerName(customerName)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));
        customer.setCustomerPassword(null);
        Response response = new Response();
        response.setSuccess(customer);
        return response;
    }

    public Response updateCustomer(Customer customer) {
        Customer existing = customerRepository.findById(customer.getCustomerId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));

        // 비밀번호·포인트만 변경 (ID는 식별자라 변경 안 함)
        if (customer.getCustomerPassword() != null) {
            existing.setCustomerPassword(customer.getCustomerPassword());
        }
        if (customer.getCustomerPoint() != null) {
            existing.setCustomerPoint(customer.getCustomerPoint());
        }
        Customer saved = customerRepository.save(existing);
        saved.setCustomerPassword(null);

        Response response = new Response();
        response.setSuccess(saved);
        return response;
    }

    public Response deleteCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));
        customerRepository.delete(customer);

        Response response = new Response();
        response.setSuccess("삭제 완료: " + customerId);
        return response;
    }

    @Transactional(readOnly = true)
    public Response getCustomerProducts(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "고객을 찾을 수 없습니다"));

        List<OrderItem> orderItems = orderItemRepository.findByCustomer_CustomerId(customerId);
        List<OrderItemDto> products = orderItems.stream()
                .map(item -> OrderItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getProductName())
                        .productPrice(item.getProduct().getProductPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        Response response = new Response();
        response.setSuccess(products);
        return response;
    }
}