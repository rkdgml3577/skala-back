package com.skala.stock.service;

import com.skala.stock.dto.TradeRequestDto;
import com.skala.stock.dto.TransactionDto;
import com.skala.stock.entity.Portfolio;
import com.skala.stock.entity.Stock;
import com.skala.stock.entity.Transaction;
import com.skala.stock.entity.User;
import com.skala.stock.repository.PortfolioRepository;
import com.skala.stock.repository.StockRepository;
import com.skala.stock.repository.TransactionRepository;
import com.skala.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    @Transactional  // ★ 매매 전체를 하나의 트랜잭션으로 — 하나라도 실패하면 전부 롤백
    public TransactionDto executeTrade(TradeRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + request.getUserId()));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + request.getStockId()));

        Long price = stock.getCurrentPrice();
        Long quantity = request.getQuantity();
        Long totalAmount = price * quantity;

        if (request.getType() == Transaction.TransactionType.BUY) {
            buy(user, stock, quantity, price, totalAmount);
        } else {
            sell(user, stock, quantity, totalAmount);
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .stock(stock)
                .type(request.getType())
                .quantity(quantity)
                .price(price)
                .totalAmount(totalAmount)
                .build();
        Transaction saved = transactionRepository.save(transaction);

        return convertToDto(saved);
    }

    private void buy(User user, Stock stock, Long quantity, Long price, Long totalAmount) {
        if (user.getBalance() < totalAmount) {
            throw new RuntimeException("잔액이 부족합니다. 필요: " + totalAmount + ", 보유: " + user.getBalance());
        }
        user.setBalance(user.getBalance() - totalAmount);

        portfolioRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                .ifPresentOrElse(
                        portfolio -> {
                            long oldQty = portfolio.getQuantity();
                            long oldTotal = oldQty * portfolio.getAveragePrice();
                            long newQty = oldQty + quantity;
                            long newAvg = (oldTotal + totalAmount) / newQty;
                            portfolio.setQuantity(newQty);
                            portfolio.setAveragePrice(newAvg);
                        },
                        () -> {
                            Portfolio newPortfolio = Portfolio.builder()
                                    .user(user)
                                    .stock(stock)
                                    .quantity(quantity)
                                    .averagePrice(price)
                                    .build();
                            portfolioRepository.save(newPortfolio);
                        }
                );
    }

    private void sell(User user, Stock stock, Long quantity, Long totalAmount) {
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow(() -> new RuntimeException("보유하지 않은 주식입니다"));

        if (portfolio.getQuantity() < quantity) {
            throw new RuntimeException("보유 수량이 부족합니다. 보유: " + portfolio.getQuantity());
        }

        user.setBalance(user.getBalance() + totalAmount);

        long remaining = portfolio.getQuantity() - quantity;
        if (remaining == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(remaining);
        }
    }

    private TransactionDto convertToDto(Transaction t) {
        return TransactionDto.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .username(t.getUser().getUsername())
                .stockId(t.getStock().getId())
                .stockCode(t.getStock().getCode())
                .stockName(t.getStock().getName())
                .type(t.getType())
                .quantity(t.getQuantity())
                .price(t.getPrice())
                .totalAmount(t.getTotalAmount())
                .transactionDate(t.getTransactionDate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}