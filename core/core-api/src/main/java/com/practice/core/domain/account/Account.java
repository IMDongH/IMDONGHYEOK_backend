package com.practice.core.domain.account;

import com.practice.core.enums.EntityStatus;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Account {
    private final Long id;
    private final String accountNumber;
    private final Long userId;
    private final BigDecimal balance;
    private final EntityStatus status;

    public Account(Long id, String accountNumber, Long userId, BigDecimal balance, EntityStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.status = status;
    }
}
