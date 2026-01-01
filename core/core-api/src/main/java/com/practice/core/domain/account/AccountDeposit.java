package com.practice.core.domain.account;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AccountDeposit {
    private final Long accountId;
    private final BigDecimal amount;
    private final String description;
}
