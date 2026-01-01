package com.practice.core.api.controller.account.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccountRequest {
    private String accountNumber;

    public CreateAccountRequest(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
