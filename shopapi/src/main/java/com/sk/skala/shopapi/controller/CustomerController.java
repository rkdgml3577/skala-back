package com.sk.skala.shopapi.controller;

import com.sk.skala.shopapi.data.Customer;
import com.sk.skala.shopapi.data.OrderRequest;
import com.sk.skala.shopapi.data.Response;
import com.sk.skala.shopapi.exception.ResponseException;
import com.sk.skala.shopapi.service.CustomerService;
import com.sk.skala.shopapi.tools.SessionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.sk.skala.shopapi.exception.Error;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final SessionHandler sessionHandler;

    @GetMapping
    public Response getAllCustomers(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int count) {
        return customerService.getAllCustomers(offset, count);
    }

    @GetMapping("/{customerId}")
    public Response getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public Response createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PostMapping("/login")
    public Response login(@RequestBody Customer customer, HttpServletResponse httpResponse) {
        return customerService.loginCustomer(customer, httpResponse);
    }

    @PostMapping("/order")
    public Response order(@RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        // JWT 쿠키에서 고객 식별
        String customerId = sessionHandler.getCustomerId(httpRequest);
        if (customerId == null) {
            throw new ResponseException(Error.NOT_AUTHENTICATED, "로그인이 필요합니다");
        }
        return customerService.placeOrder(customerId, request);
    }

    @PostMapping("/cancel")
    public Response cancel(@RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        String customerId = sessionHandler.getCustomerId(httpRequest);
        if (customerId == null) {
            throw new ResponseException(Error.NOT_AUTHENTICATED, "로그인이 필요합니다");
        }
        return customerService.cancelOrder(customerId, request);
    }

    // ① 이름으로 조회
    @GetMapping("/name/{customerName}")
    public Response getCustomerByName(@PathVariable String customerName) {
        return customerService.getCustomerByName(customerName);
    }

    // ② 정보 변경
    @PutMapping
    public Response updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }

    // ③ 삭제
    @DeleteMapping("/{customerId}")
    public Response deleteCustomer(@PathVariable String customerId) {
        return customerService.deleteCustomer(customerId);
    }

    // ④ 고객의 상품 목록
    @GetMapping("/{customerId}/products")
    public Response getCustomerProducts(@PathVariable String customerId) {
        return customerService.getCustomerProducts(customerId);
    }
}