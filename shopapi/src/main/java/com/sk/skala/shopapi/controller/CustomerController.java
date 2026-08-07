package com.sk.skala.shopapi.controller;

import com.sk.skala.shopapi.data.Customer;
import com.sk.skala.shopapi.data.Response;
import com.sk.skala.shopapi.service.CustomerService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

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
}