package com.practice.core.domain.account;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewAccount {
    private final String accountNumber;
}
