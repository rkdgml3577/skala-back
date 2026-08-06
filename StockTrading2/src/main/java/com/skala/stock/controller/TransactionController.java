package com.skala.stock.controller;

import com.skala.stock.dto.TradeRequestDto;
import com.skala.stock.dto.TransactionDto;
import com.skala.stock.service.TradeService;
import com.skala.stock.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "거래 관리", description = "주식 매수/매도 거래 API")
public class TransactionController {

    private final TransactionService transactionService;
    private final TradeService tradeService; // ← 추가

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 거래 내역 조회", description = "특정 사용자의 전체 거래 내역을 조회합니다")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@PathVariable Long userId) {
        List<TransactionDto> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    // === 과제 7: 매매 실행 ===
    @PostMapping("/trade")
    @Operation(summary = "주식 매매 실행", description = "매수(BUY)/매도(SELL)를 실행합니다")
    public ResponseEntity<TransactionDto> executeTrade(@Valid @RequestBody TradeRequestDto request) {
        TransactionDto result = tradeService.executeTrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "거래 상세 조회", description = "ID로 특정 거래를 조회합니다")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}