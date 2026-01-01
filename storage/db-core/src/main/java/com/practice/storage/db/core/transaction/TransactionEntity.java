package com.practice.storage.db.core.transaction;

import com.practice.core.enums.TransactionDirection;
import com.practice.core.enums.TransactionType;
import com.practice.storage.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transaction")
@SQLRestriction("status = 'ACTIVE'")
public class TransactionEntity extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionDirection direction;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal fee;

    @Column(nullable = false)
    private BigDecimal balanceSnapshot;

    @Column
    private Long counterpartyAccountId;

    @Column
    private String description;

    @Builder
    public TransactionEntity(Long accountId, TransactionType type, TransactionDirection direction, BigDecimal amount,
            BigDecimal fee, BigDecimal balanceSnapshot, Long counterpartyAccountId, String description) {
        this.accountId = accountId;
        this.type = type;
        this.direction = direction;
        this.amount = amount;
        this.fee = fee;
        this.balanceSnapshot = balanceSnapshot;
        this.counterpartyAccountId = counterpartyAccountId;
        this.description = description;
    }
}
