package com.sk.skala.shopapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    // ★ 핵심: 메인 스레드가 아닌 별도의 스레드(task-x)에서 실행됨
    @Async
    public void sendOrderCompleteNotification(String customerId) {
        log.info("🔔 [알림 시스템] {} 고객님께 알림톡 발송을 시작합니다...", customerId);
        try {
            // 외부 알림 서버의 응답 지연(3초)을 시뮬레이션
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("✅ [알림 시스템] {} 고객님 알림톡 발송 완료!", customerId);
    }
}