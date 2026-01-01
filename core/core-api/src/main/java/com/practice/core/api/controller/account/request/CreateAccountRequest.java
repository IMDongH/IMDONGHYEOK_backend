package com.practice.core.api.controller.account.request;

import com.practice.core.domain.account.NewAccount;
import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccountRequest {
    private String accountNumber;

    public CreateAccountRequest(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public NewAccount toNewAccount() {
        if(accountNumber == null) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        return NewAccount.builder()
            .accountNumber(accountNumber)
            .build();
    }

}
