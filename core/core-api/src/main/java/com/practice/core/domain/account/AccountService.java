package com.practice.core.domain.account;

import com.practice.core.api.controller.account.response.TransactionResponse;
import com.practice.core.domain.transaction.Transaction;
import com.practice.core.support.OffsetLimit;
import com.practice.core.support.Page;
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
import java.math.RoundingMode;
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
    private final com.practice.core.domain.limit.DailyLimitUsageService dailyLimitUsageService;
    private final com.practice.core.domain.transaction.TransactionReader transactionReader;

    private static final BigDecimal DAILY_WITHDRAW_LIMIT = BigDecimal.valueOf(1000000);

    @Transactional
    public Long createAccount(NewAccount newAccount) {
        if (accountReader.existsByAccountNumber(newAccount.getAccountNumber())) {
            throw new CoreException(ErrorType.DUPLICATE_ACCOUNT_NUMBER); // 중복된 계좌번호 등록 불가
        }

        AccountEntity accountEntity = new AccountEntity(
                newAccount.getAccountNumber(),
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
    public Long deposit(AccountDeposit deposit) {
        AccountEntity account = accountReader.readWithLock(deposit.getAccountId());
        account.deposit(deposit.getAmount());

        TransactionEntity transaction = TransactionEntity
                .builder()
                .accountId(deposit.getAccountId())
                .type(TransactionType.DEPOSIT)
                .direction(TransactionDirection.IN)
                .amount(deposit.getAmount())
                .fee(BigDecimal.ZERO)
                .balanceSnapshot(account.getBalance())
                .description(deposit.getDescription())
                .build();

        transactionWriter.save(transaction);

        return account.getId();
    }

    @Transactional
    public Long withdraw(AccountWithdraw withdraw) {
        AccountEntity account = accountReader.readWithLock(withdraw.getAccountId());
        // Check Daily Limit
        LocalDate today = LocalDate.now();
        dailyLimitUsageService.ensureDailyLimitUsageExists(withdraw.getAccountId(), today);
        DailyLimitUsageEntity dailyLimitUsage = dailyLimitUsageReader
                .findByAccountIdAndDateWithLock(withdraw.getAccountId(), today)
                .orElseThrow();

        if (dailyLimitUsage.getTotalWithdrawAmount().add(withdraw.getAmount()).compareTo(DAILY_WITHDRAW_LIMIT) > 0) {
            throw new CoreException(ErrorType.EXCEED_DAILY_WITHDRAW_LIMIT);
        }

        if (account.getBalance().compareTo(withdraw.getAmount()) < 0) {
            throw new CoreException(ErrorType.INSUFFICIENT_BALANCE);
        }

        account.withdraw(withdraw.getAmount());
        dailyLimitUsage.addWithdrawAmount(withdraw.getAmount());

        dailyLimitUsageWriter.save(dailyLimitUsage);

        TransactionEntity transaction = TransactionEntity.builder()
                .accountId(withdraw.getAccountId())
                .type(TransactionType.WITHDRAW)
                .direction(TransactionDirection.OUT)
                .amount(withdraw.getAmount())
                .fee(BigDecimal.ZERO)
                .balanceSnapshot(account.getBalance())
                .description(withdraw.getDescription())
                .build();

        transactionWriter.save(transaction);

        return account.getId();
    }

    private static final BigDecimal TRANSFER_FEE_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal DAILY_TRANSFER_LIMIT = BigDecimal.valueOf(3_000_000);

    @Transactional
    public void transfer(AccountTransfer transfer) {
        Long senderId = transfer.getSenderAccountId();
        String receiverAccountNumber = transfer.getReceiverAccountNumber();

        Long receiverId = accountReader.readIdByAccountNumber(receiverAccountNumber);

        // Deadlock 방지
        AccountEntity sender;
        AccountEntity receiver;

        if (senderId < receiverId) {
            sender = accountReader.readWithLock(senderId);
            receiver = accountReader.readWithLock(receiverId);
        } else {
            receiver = accountReader.readWithLock(receiverId);
            sender = accountReader.readWithLock(senderId);
        }

        BigDecimal fee = transfer.getAmount().multiply(TRANSFER_FEE_RATE).setScale(0, RoundingMode.FLOOR);
        BigDecimal totalAmount = transfer.getAmount().add(fee);

        // GAP LOCK 방지
        LocalDate today = LocalDate.now();
        dailyLimitUsageService.ensureDailyLimitUsageExists(senderId, today);
        DailyLimitUsageEntity senderDailyLimitUsage = dailyLimitUsageReader
                .findByAccountIdAndDateWithLock(senderId, today)
                .orElseThrow();

        if (senderDailyLimitUsage.getTotalTransferAmount().add(transfer.getAmount())
                .compareTo(DAILY_TRANSFER_LIMIT) > 0) {
            throw new CoreException(ErrorType.EXCEED_DAILY_TRANSFER_LIMIT);
        }

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            throw new CoreException(ErrorType.INSUFFICIENT_BALANCE);
        }

        sender.withdraw(totalAmount);
        receiver.deposit(transfer.getAmount());

        senderDailyLimitUsage.addTransferAmount(transfer.getAmount());
        dailyLimitUsageWriter.save(senderDailyLimitUsage);

        TransactionEntity senderTransaction = TransactionEntity.builder()
                .accountId(senderId)
                .counterpartyAccountId(receiverId)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.OUT)
                .amount(transfer.getAmount())
                .fee(fee)
                .balanceSnapshot(sender.getBalance())
                .description(transfer.getDescription())
                .build();
        transactionWriter.save(senderTransaction);

        TransactionEntity receiverTransaction = TransactionEntity.builder()
                .accountId(receiverId)
                .counterpartyAccountId(senderId)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.IN)
                .amount(transfer.getAmount())
                .fee(BigDecimal.ZERO)
                .balanceSnapshot(receiver.getBalance())
                .description(transfer.getDescription())
                .build();
        transactionWriter.save(receiverTransaction);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> readTransactions(Long accountId, TransactionType type,
        OffsetLimit offsetLimit) {

        return transactionReader.readByAccountIdAndType(
            accountId, type,offsetLimit);
    }
}
