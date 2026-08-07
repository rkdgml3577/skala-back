package com.sk.skala.shopapi.controller;

import com.sk.skala.shopapi.data.Product;
import com.sk.skala.shopapi.data.Response;
import com.sk.skala.shopapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Response getAllProducts(
        
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int count) {
                
        return productService.getAllProducts(offset, count);
    }

    @GetMapping("/{id}")
    public Response getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Response createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping
    public Response updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    public Response deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
}