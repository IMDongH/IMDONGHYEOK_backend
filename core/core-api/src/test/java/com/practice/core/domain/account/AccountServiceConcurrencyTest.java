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
}
