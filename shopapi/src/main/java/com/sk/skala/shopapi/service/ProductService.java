package com.sk.skala.shopapi.service;

import com.sk.skala.shopapi.data.Product;
import com.sk.skala.shopapi.data.Response;
import com.sk.skala.shopapi.exception.Error;
import com.sk.skala.shopapi.exception.ResponseException;
import com.sk.skala.shopapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 전체 상품 조회 (페이징)
    public Response getAllProducts(int offset, int count) {
        Pageable pageable = PageRequest.of(offset, count);
        Page<Product> page = productRepository.findAll(pageable);

        Response response = new Response();
        response.setSuccess(page.getContent());
        return response;
    }

    // 개별 상품 조회
    public Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        Response response = new Response();
        response.setSuccess(product);
        return response;
    }

    // 상품 등록
    public Response createProduct(Product product) {
        // 입력값 검증
        if (product.getProductName() == null || product.getProductName().isEmpty()
                || product.getProductPrice() == null || product.getProductPrice() <= 0) {
            throw new ResponseException(Error.DATA_NOT_FOUND, "상품명과 가격은 필수입니다");
        }
        // 이름 중복 체크
        if (productRepository.findByProductName(product.getProductName()).isPresent()) {
            throw new ResponseException(Error.DATA_DUPLICATED);
        }
        product.setId(0L);  // 신규 → JPA가 자동 생성
        Product saved = productRepository.save(product);

        Response response = new Response();
        response.setSuccess(saved);
        return response;
    }

    // 상품 수정
    public Response updateProduct(Product product) {
        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        existing.setProductName(product.getProductName());
        existing.setProductPrice(product.getProductPrice());
        Product saved = productRepository.save(existing);

        Response response = new Response();
        response.setSuccess(saved);
        return response;
    }

    // 상품 삭제
    public Response deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));
        productRepository.delete(product);

        Response response = new Response();
        response.setSuccess("삭제 완료: " + id);
        return response;
    }
}