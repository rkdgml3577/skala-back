package com.sk.skala.shopapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 인증 인터셉터를 거쳐야 하는 API 경로 설정
                .addPathPatterns(
                        "/api/customers/order", 
                        "/api/customers/cancel", 
                        "/api/customers/{id}" // 내 주문 확인 등
                )
                // 인증 없이 누구나 접근 가능한 경로 설정 (회원가입, 로그인, 상품 조회, Swagger 등)
                .excludePathPatterns(
                        "/api/customers", 
                        "/api/customers/login", 
                        "/api/products/**",
                        "/swagger-ui/**", 
                        "/v3/api-docs/**"
                );
    }
}