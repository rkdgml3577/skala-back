package com.skala.stock.controller;

import com.skala.stock.dto.TradeSnapshotDto;
import com.skala.stock.service.StockAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "분석", description = "포트폴리오/거래 분석 API (MyBatis)")
public class StockAnalysisController {

    private final StockAnalysisService analysisService;

    @GetMapping("/portfolio/{userId}/profit")
    @Operation(summary = "포트폴리오 평가 손익", description = "종목별 평가액·손익")
    public ResponseEntity<List<Map<String, Object>>> getPortfolioProfit(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getPortfolioProfit(userId));
    }

    @GetMapping("/transactions/{userId}")
    @Operation(summary = "거래 내역 상세", description = "전체 거래 상세")
    public ResponseEntity<List<Map<String, Object>>> getTransactionDetails(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getTransactionDetails(userId));
    }

    @GetMapping("/transactions/{userId}/stock/{stockId}")
    @Operation(summary = "특정 주식 거래 내역", description = "종목별 거래 내역")
    public ResponseEntity<List<Map<String, Object>>> getStockTransactions(
            @PathVariable Long userId, @PathVariable Long stockId) {
        return ResponseEntity.ok(analysisService.getStockTransactions(userId, stockId));
    }

    @GetMapping("/assets/{userId}")
    @Operation(summary = "총 자산 조회", description = "현금+주식평가액")
    public ResponseEntity<Long> getTotalAssets(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getTotalAssets(userId));
    }

    @GetMapping("/return/{userId}")
    @Operation(summary = "총 수익률 조회", description = "(현재가치-매수금액)/매수금액 %")
    public ResponseEntity<Double> getReturnRate(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getTotalReturnRate(userId));
    }

    @GetMapping("/snapshot/{userId}")
    @Operation(summary = "자산 스냅샷", description = "총자산+수익률 묶음")
    public ResponseEntity<TradeSnapshotDto> getSnapshot(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getSnapshot(userId));
    }

    @GetMapping("/statistics/{userId}")
    @Operation(summary = "거래 통계", description = "매수/매도별 건수·총액")
    public ResponseEntity<List<Map<String, Object>>> getStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getStatistics(userId));
    }

    @GetMapping("/daily/{userId}")
    @Operation(summary = "일별 거래 내역", description = "날짜별 집계")
    public ResponseEntity<List<Map<String, Object>>> getDaily(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getDaily(userId));
    }
}