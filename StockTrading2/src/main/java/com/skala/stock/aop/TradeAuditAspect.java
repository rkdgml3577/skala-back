package com.skala.stock.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect      // AOP 클래스임을 명시
@Component   // 스프링 빈으로 등록 (필수!)
public class TradeAuditAspect {

    // com.skala.stock.service 패키지 하위의 모든 메서드에 적용
    @Around("execution(* com.skala.stock.service..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("▶️ [AOP START] 실행 메서드: {}", joinPoint.getSignature().toShortString());
        
        long start = System.currentTimeMillis();
        
        // 실제 메서드 실행
        Object result = joinPoint.proceed(); 
        
        long end = System.currentTimeMillis();
        log.info("⏹️ [AOP END] 실행 시간: {}ms", (end - start));
        
        return result;
    }
}