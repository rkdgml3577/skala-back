package com.sk.skala.shopapi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // controller 패키지의 모든 메서드를 가로챈다
    @Around("execution(* com.sk.skala.shopapi.controller..*(..))")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs(); // 파라미터 추출
        long start = System.currentTimeMillis();

        log.info("▶ API 호출 시작: {} | 요청 데이터: {}", method, Arrays.toString(args));
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            if (elapsed > 200) {
                log.warn("⚠ API 지연 발생: {} ({}ms)", method, elapsed);
            } else {
                log.info("◀ API 호출 완료: {} ({}ms)", method, elapsed);
            }
            return result;
        } catch (Exception e) {
            log.error("✖ API 호출 실패: {} | 에러: {}", method, e.getMessage());
            throw e;
        }
    }
}