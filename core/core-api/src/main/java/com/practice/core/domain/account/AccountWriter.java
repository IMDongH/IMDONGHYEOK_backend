package com.practice.core.domain.account;

import com.practice.storage.db.core.account.AccountEntity;
import com.practice.storage.db.core.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountWriter {

    private final AccountRepository accountRepository;

    public AccountEntity save(AccountEntity accountEntity) {
        return accountRepository.save(accountEntity);
    }
}
