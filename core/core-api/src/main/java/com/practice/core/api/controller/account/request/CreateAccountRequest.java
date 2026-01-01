package com.practice.core.api.controller.account.request;

import com.practice.core.domain.account.NewAccount;
import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor
public class CreateAccountRequest {
    private String accountNumber;

    public CreateAccountRequest(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public NewAccount toNewAccount() {
        if(StringUtils.isBlank(accountNumber)) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        return NewAccount.builder()
            .accountNumber(accountNumber)
            .build();
    }

}
