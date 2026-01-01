package com.practice.core.domain.account;

import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import com.practice.storage.db.core.account.AccountEntity;
import com.practice.core.domain.limit.DailyLimitUsageReader;
import com.practice.core.domain.limit.DailyLimitUsageWriter;
import com.practice.core.domain.transaction.TransactionWriter;
import com.practice.core.enums.TransactionDirection;
import com.practice.core.enums.TransactionType;
import com.practice.storage.db.core.limit.DailyLimitUsageEntity;
import com.practice.storage.db.core.transaction.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountReader accountReader;
    private final AccountWriter accountWriter;
    private final TransactionWriter transactionWriter;
    private final DailyLimitUsageReader dailyLimitUsageReader;
    private final DailyLimitUsageWriter dailyLimitUsageWriter;

    private static final BigDecimal DAILY_WITHDRAW_LIMIT = BigDecimal.valueOf(1_000_000);

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

    @Transactional
    public Long deposit(Long accountId, BigDecimal amount, String description) {
        AccountEntity account = accountReader.readWithLock(accountId);
        account.deposit(amount);

        TransactionEntity transaction = TransactionEntity
                .builder()
                .accountId(accountId)
                .type(TransactionType.DEPOSIT)
                .direction(TransactionDirection.IN)
                .amount(amount)
                .fee(BigDecimal.ZERO)
                .balanceSnapshot(account.getBalance())
                .description(description)
                .build();

        transactionWriter.save(transaction);

        return account.getId();
    }


}
