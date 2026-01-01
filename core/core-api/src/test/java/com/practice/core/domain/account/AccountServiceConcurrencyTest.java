package com.practice.core.domain.account;

import com.practice.storage.db.core.account.AccountEntity;
import com.practice.storage.db.core.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceConcurrencyTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("동시에 100번 입금 요청 시 잔액이 정확해야 한다")
    void deposit_concurrency() throws InterruptedException {
        // given
        String accountNumber = "CONCURRENCY-TEST-" + System.currentTimeMillis();
        AccountEntity account = new AccountEntity(accountNumber, BigDecimal.ZERO);
        accountRepository.save(account);
        Long accountId = account.getId();

        int threadCount = 100;
        BigDecimal amount = BigDecimal.ONE;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    AccountDeposit command = AccountDeposit.builder()
                            .accountId(accountId)
                            .amount(amount)
                            .description("Deposit")
                            .build();
                    accountService.deposit(command);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        AccountEntity result = accountRepository.findById(accountId).orElseThrow();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(threadCount));
    }

    @Test
    @DisplayName("동시에 100번 출금 요청 시 잔액이 정확해야 한다")
    void withdraw_concurrency() throws InterruptedException {
        // given
        String accountNumber = "WITHDRAW-CONCURRENCY-" + System.currentTimeMillis();
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        AccountEntity account = new AccountEntity(accountNumber, initialBalance);
        accountRepository.save(account);
        Long accountId = account.getId();

        int threadCount = 100;
        BigDecimal amount = BigDecimal.ONE;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    AccountWithdraw command = AccountWithdraw.builder()
                            .accountId(accountId)
                            .amount(amount)
                            .description("Withdraw")
                            .build();
                    accountService.withdraw(command);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        AccountEntity result = accountRepository.findById(accountId).orElseThrow();
        BigDecimal expectedBalance = initialBalance.subtract(BigDecimal.valueOf(threadCount).multiply(amount));
        assertThat(result.getBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("동시에 100번 이체 요청 시 잔액이 정확해야 한다")
    void transfer_concurrency() throws InterruptedException {
        // given
        String senderAccountNumber = "TRANSFER-SENDER-" + System.currentTimeMillis();
        String receiverAccountNumber = "TRANSFER-RECEIVER-" + System.currentTimeMillis();
        BigDecimal initialBalance = BigDecimal.valueOf(20000); // 100 * (100 + 1) = 10100 필요
        AccountEntity sender = new AccountEntity(senderAccountNumber, initialBalance);
        AccountEntity receiver = new AccountEntity(receiverAccountNumber, BigDecimal.ZERO);
        accountRepository.save(sender);
        accountRepository.save(receiver);
        Long senderId = sender.getId();
        Long receiverId = receiver.getId();

        int threadCount = 100;
        BigDecimal amount = BigDecimal.valueOf(100); // 100 KRW
        // Fee = 1 KRW
        // Total deduction per transfer = 101 KRW
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    AccountTransfer command = AccountTransfer.builder()
                            .senderAccountId(senderId)
                            .receiverAccountNumber(receiverAccountNumber)
                            .amount(amount)
                            .description("Transfer")
                            .build();
                    accountService.transfer(command);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        AccountEntity senderResult = accountRepository.findById(senderId).orElseThrow();
        AccountEntity receiverResult = accountRepository.findById(receiverId).orElseThrow();

        BigDecimal expectedSenderBalance = initialBalance
                .subtract(BigDecimal.valueOf(threadCount).multiply(BigDecimal.valueOf(101)));
        BigDecimal expectedReceiverBalance = BigDecimal.valueOf(threadCount).multiply(amount);

        assertThat(senderResult.getBalance()).isEqualByComparingTo(expectedSenderBalance);
        assertThat(receiverResult.getBalance()).isEqualByComparingTo(expectedReceiverBalance);
    }
}
