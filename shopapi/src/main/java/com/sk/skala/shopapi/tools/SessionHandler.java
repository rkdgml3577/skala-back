package com.sk.skala.shopapi.tools;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionHandler {

    private final JwtTool jwtTool;
    private static final String COOKIE_NAME = "bff-access";

    // 로그인 성공 시: 토큰을 쿠키에 담아 응답
    public void storeAccessToken(HttpServletResponse response, String customerId) {
        String token = jwtTool.createToken(customerId);
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간
        response.addCookie(cookie);
    }

    // 요청에서 쿠키의 토큰을 꺼내 customerId 반환 (주문/취소 시 사용)
    public String getCustomerId(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return jwtTool.getCustomerId(cookie.getValue());
            }
        }
        return null;
    }
}