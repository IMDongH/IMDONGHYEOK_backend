package com.practice.core.api.controller.account.request;

import com.practice.core.domain.account.AccountWithdraw;
import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class WithdrawRequest {
    private BigDecimal amount;
    private String description;

    public WithdrawRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public AccountWithdraw toAccountWithdraw(Long accountId) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0) { //0보다 작거나 같은 값인 경우
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        return AccountWithdraw.builder()
            .accountId(accountId)
            .amount(amount)
            .description(description)
            .build();
    }
}
