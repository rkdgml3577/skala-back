package com.sk.skala.shopapi.tools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTool {

    // 서명에 쓸 비밀키 (실무에선 설정파일/환경변수로 분리)
    private final SecretKey key = Keys.hmacShaKeyFor(
            "skala-shop-secret-key-must-be-long-enough-256bit!!".getBytes());

    private final long EXPIRE_MS = 1000 * 60 * 60;  // 1시간

    // 토큰 발급: customerId를 담아서
    public String createToken(String customerId) {
        return Jwts.builder()
                .subject(customerId)                          // 누구인지
                .issuedAt(new Date())                         // 발급 시각
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))  // 만료
                .signWith(key)                                // 서명
                .compact();
    }

    // 토큰 검증 + customerId 추출
    public String getCustomerId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)      // 서명 검증 (위조면 여기서 예외)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();   // 담아뒀던 customerId 꺼내기
    }
}