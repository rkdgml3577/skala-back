package com.sk.skala.shopapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI shopApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Skala Online Shop API")
                        .description("고객의 상품 주문 여정을 처리하는 쇼핑몰 API 명세서")
                        .version("v1.0.0"));
    }
}