package com.sk.skala.shopapi.config;

import com.sk.skala.shopapi.tools.SessionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionHandler sessionHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 프리플라이트(Preflight) 요청인 OPTIONS 메서드는 통과시킵니다.
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        try {
            // SessionHandler를 통해 쿠키에서 JWT 파싱 후 customerId 추출
            String customerId = sessionHandler.getCustomerId(request);
            
            if (customerId == null) {
                log.warn("인증 실패: 유효한 토큰이 존재하지 않음. 접근 URI: {}", request.getRequestURI());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
                return false;
            }
            
            // 검증된 고객 ID를 request 속성에 담아 컨트롤러에서 편하게 사용할 수 있도록 함
            request.setAttribute("customerId", customerId);
            return true;
            
        } catch (Exception e) {
            log.error("토큰 검증 중 서버 오류 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 검증 오류");
            return false;
        }
    }
}