package com.practice.core.api.controller.account.request;

import com.practice.core.domain.account.AccountTransfer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TransferRequest {
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String description;

    public TransferRequest(String receiverAccountNumber, BigDecimal amount, String description) {
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.description = description;
    }

    public AccountTransfer toAccountTransfer(Long senderAccountId) {
        return AccountTransfer.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountNumber(receiverAccountNumber)
                .amount(amount)
                .description(description)
                .build();
    }
}
