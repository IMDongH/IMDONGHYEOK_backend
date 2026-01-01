package com.practice.core.domain.account;

import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import com.practice.storage.db.core.account.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountReader accountReader;
    private final AccountWriter accountWriter;

    @Transactional
    public Long createAccount(String accountNumber) {
        if (accountReader.existsByAccountNumber(accountNumber)) {
            throw new CoreException(ErrorType.DUPLICATE_ACCOUNT_NUMBER); // 중복된 계좌번호 등록 불가
        }

        AccountEntity accountEntity = new AccountEntity(
                accountNumber,
                BigDecimal.ZERO);
        return accountWriter.save(accountEntity).getId();
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        AccountEntity accountEntity = accountReader.read(accountId);

        if (accountEntity.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST); // 잔액이 남아있으면 삭제 불가
        }

        accountEntity.delete();
    }
}
