package com.skala.stock.service;

import com.skala.stock.dto.TradeSnapshotDto;
import com.skala.stock.entity.User;
import com.skala.stock.mapper.StockMapper;
import com.skala.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockAnalysisService {

    private final StockMapper stockMapper;
    private final UserRepository userRepository;


     // 1번: 포트폴리오 손익
    public List<Map<String, Object>> getPortfolioProfit(Long userId) {
        return stockMapper.getPortfolioProfit(userId);
    }

    // 2번: 거래 내역 상세
    public List<Map<String, Object>> getTransactionDetails(Long userId) {
        return stockMapper.getTransactionDetails(userId);
    }

    // 3번 & 8번: 특정 주식 거래 내역
    public List<Map<String, Object>> getStockTransactions(Long userId, Long stockId) {
        return stockMapper.getStockTransactions(userId, stockId);
    }
    // 4번: 총 자산 (현금 + 주식평가액)
    public Long getTotalAssets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음: " + userId));
        Long stockValue = stockMapper.getTotalStockValue(userId);
        return user.getBalance() + stockValue;
    }

    // 5번: 총 수익률 (%)
    public Double getTotalReturnRate(Long userId) {
        Long buyAmount = stockMapper.getTotalBuyAmount(userId);
        if (buyAmount == 0) return 0.0;
        Long currentValue = stockMapper.getTotalStockValue(userId);
        return (currentValue - buyAmount) * 100.0 / buyAmount;
    }

    // 4+5 묶음: TradeSnapshotDto
    public TradeSnapshotDto getSnapshot(Long userId) {
        return TradeSnapshotDto.builder()
                .userId(userId)
                .totalAssets(getTotalAssets(userId))
                .totalReturnRate(getTotalReturnRate(userId))
                .build();
    }

    // 6번: 거래 통계
    public List<Map<String, Object>> getStatistics(Long userId) {
        return stockMapper.getTradeStatistics(userId);
    }

    // 7번: 일별 거래
    public List<Map<String, Object>> getDaily(Long userId) {
        return stockMapper.getDailyTransactions(userId);
    }
}