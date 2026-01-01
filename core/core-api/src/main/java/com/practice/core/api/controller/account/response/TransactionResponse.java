package com.practice.core.api.controller.account.response;

import com.practice.core.domain.transaction.Transaction;
import com.practice.storage.db.core.transaction.TransactionEntity;
import com.practice.core.enums.TransactionType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceSnapshot;
    private String description;
    private LocalDateTime createdAt;

    public static TransactionResponse of(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }


    public static List<TransactionResponse> of(List<Transaction> reviews) {
        return reviews.stream().map(TransactionResponse::of).collect(Collectors.toList());
    }
}
