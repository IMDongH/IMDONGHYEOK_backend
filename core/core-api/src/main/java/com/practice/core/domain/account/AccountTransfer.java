package com.practice.core.domain.account;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AccountTransfer {
    private final Long senderAccountId;
    private final String receiverAccountNumber;
    private final BigDecimal amount;
    private final String description;
}
