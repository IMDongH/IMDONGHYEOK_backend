package com.practice.storage.db.core.limit;

import com.practice.storage.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "daily_limit_usage")
public class DailyLimitUsageEntity extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal totalWithdrawAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalTransferAmount = BigDecimal.ZERO;

    @Version
    private Long version;

    public DailyLimitUsageEntity(Long accountId, LocalDate date) {
        this.accountId = accountId;
        this.date = date;
    }

    public void addWithdrawAmount(BigDecimal amount) {
        this.totalWithdrawAmount = this.totalWithdrawAmount.add(amount);
    }

    public void addTransferAmount(BigDecimal amount) {
        this.totalTransferAmount = this.totalTransferAmount.add(amount);
    }
}
