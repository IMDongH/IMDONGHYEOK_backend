package com.practice.core.domain.transaction;

import com.practice.core.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Transaction {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceSnapshot;
    private String description;
    private LocalDateTime createdAt;

}
