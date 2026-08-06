package com.skala.stock.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface StockMapper {

    // 4번: 총 주식 평가액
    Long getTotalStockValue(Long userId);

    // 5번: 총 매수금액
    Long getTotalBuyAmount(Long userId);

    // 6번: 거래 통계 (BUY/SELL별 건수·총액)
    List<Map<String, Object>> getTradeStatistics(Long userId);

    // 7번: 일별 거래 내역
    List<Map<String, Object>> getDailyTransactions(Long userId);
    
    // 1번: 포트폴리오 평가 손익
    List<Map<String, Object>> getPortfolioProfit(Long userId);

    // 2번: 거래 내역 상세 (전체)
    List<Map<String, Object>> getTransactionDetails(Long userId);

    // 3번 & 8번: 특정 주식 거래 내역 (파라미터가 2개이므로 @Param 사용 권장)
    List<Map<String, Object>> getStockTransactions(
            @org.apache.ibatis.annotations.Param("userId") Long userId, 
            @org.apache.ibatis.annotations.Param("stockId") Long stockId
    );
}