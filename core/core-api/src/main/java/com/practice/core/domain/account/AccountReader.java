package com.practice.core.domain.account;

import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import com.practice.storage.db.core.account.AccountEntity;
import com.practice.storage.db.core.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountReader {

    private final AccountRepository accountRepository;

    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    public AccountEntity read(Long accountId) {
        return accountRepository.findByIdAndStatus(accountId, com.practice.core.enums.EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.ACCOUNT_NOT_FOUND));
    }
}
