package com.practice.core.api.controller.account.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class DepositRequest {
    private BigDecimal amount;
    private String description;

    public DepositRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }
}
