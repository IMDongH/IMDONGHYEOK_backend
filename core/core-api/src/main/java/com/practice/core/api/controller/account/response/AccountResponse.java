package com.practice.core.api.controller.account.response;

import com.practice.core.enums.EntityStatus;
import com.practice.storage.db.core.account.AccountEntity;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AccountResponse {
    private final Long id;
    private final String accountNumber;

    private final BigDecimal balance;
    private final EntityStatus status;

    public AccountResponse(Long id, String accountNumber, BigDecimal balance, EntityStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
    }

    public static AccountResponse from(AccountEntity accountEntity) {
        return new AccountResponse(
                accountEntity.getId(),
                accountEntity.getAccountNumber(),
                accountEntity.getBalance(),
                accountEntity.getStatus());
    }
}
