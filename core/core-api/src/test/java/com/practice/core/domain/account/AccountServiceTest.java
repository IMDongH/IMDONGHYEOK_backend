package com.practice.core.domain.account;

import com.practice.core.enums.EntityStatus;
import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;

import com.practice.storage.db.core.account.AccountEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountReader accountReader;

    @Mock
    private AccountWriter accountWriter;

    @Mock
    private com.practice.core.domain.transaction.TransactionWriter transactionWriter;

    @Mock
    private com.practice.core.domain.limit.DailyLimitUsageReader dailyLimitUsageReader;

    @Mock
    private com.practice.core.domain.limit.DailyLimitUsageWriter dailyLimitUsageWriter;

    @Mock
    private com.practice.core.domain.limit.DailyLimitUsageService dailyLimitUsageService;

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccount() {
        // given
        String accountNumber = "123-456-789";
        AccountEntity savedAccount = new AccountEntity(accountNumber, BigDecimal.ZERO);

        given(accountReader.existsByAccountNumber(accountNumber)).willReturn(false);
        given(accountWriter.save(any(AccountEntity.class))).willReturn(savedAccount);

        // when
        NewAccount command = NewAccount.builder()
                .accountNumber(accountNumber)
                .build();
        Long accountId = accountService.createAccount(command);

        // then
        assertThat(accountId).isEqualTo(savedAccount.getId());
        verify(accountWriter, times(1)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("중복된 계좌번호로 생성 시 예외 발생")
    void createAccount_duplicate() {
        // given
        String accountNumber = "123-456-789";

        given(accountReader.existsByAccountNumber(accountNumber)).willReturn(true);

        // when & then
        NewAccount command = NewAccount.builder()
                .accountNumber(accountNumber)
                .build();
        assertThatThrownBy(() -> accountService.createAccount(command))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.DUPLICATE_ACCOUNT_NUMBER.getMessage());
    }

    @Test
    @DisplayName("계좌 삭제 성공")
    void deleteAccount() {
        // given
        Long accountId = 1L;
        AccountEntity accountEntity = new AccountEntity("123", BigDecimal.ZERO);

        given(accountReader.read(accountId)).willReturn(accountEntity);

        // when
        accountService.deleteAccount(accountId);

        // then
        assertThat(accountEntity.getStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    @DisplayName("잔액이 있는 계좌 삭제 시 예외 발생")
    void deleteAccount_hasBalance() {
        // given
        Long accountId = 1L;
        AccountEntity accountEntity = new AccountEntity("123", BigDecimal.TEN);

        given(accountReader.read(accountId)).willReturn(accountEntity);

        // when & then
        assertThatThrownBy(() -> accountService.deleteAccount(accountId))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.INVALID_REQUEST.getMessage());
    }

    @Test
    @DisplayName("출금 성공")
    void withdraw_success() {
        // given
        Long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1000);
        AccountEntity account = new AccountEntity("123", BigDecimal.valueOf(2000));
        com.practice.storage.db.core.limit.DailyLimitUsageEntity dailyLimitUsage = new com.practice.storage.db.core.limit.DailyLimitUsageEntity(
                accountId, java.time.LocalDate.now());

        given(accountReader.readWithLock(accountId)).willReturn(account);
        willDoNothing().given(dailyLimitUsageService).ensureDailyLimitUsageExists(any(), any());
        given(dailyLimitUsageReader.findByAccountIdAndDateWithLock(any(), any()))
                .willReturn(java.util.Optional.of(dailyLimitUsage));

        // when
        AccountWithdraw command = AccountWithdraw.builder()
                .accountId(accountId)
                .amount(amount)
                .description("test")
                .build();
        accountService.withdraw(command);

        // then
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(dailyLimitUsage.getTotalWithdrawAmount()).isEqualByComparingTo(amount);
        verify(dailyLimitUsageWriter, times(1)).save(any());
        verify(transactionWriter, times(1)).save(any());
    }

    @Test
    @DisplayName("잔액 부족 시 출금 실패")
    void withdraw_insufficient_balance() {
        // given
        Long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(3000);
        AccountEntity account = new AccountEntity("123", BigDecimal.valueOf(2000));
        com.practice.storage.db.core.limit.DailyLimitUsageEntity dailyLimitUsage = new com.practice.storage.db.core.limit.DailyLimitUsageEntity(
                accountId, java.time.LocalDate.now());

        given(accountReader.readWithLock(accountId)).willReturn(account);
        willDoNothing().given(dailyLimitUsageService).ensureDailyLimitUsageExists(any(), any());
        given(dailyLimitUsageReader.findByAccountIdAndDateWithLock(any(), any()))
                .willReturn(java.util.Optional.of(dailyLimitUsage));

        // when & then
        AccountWithdraw command = AccountWithdraw.builder()
                .accountId(accountId)
                .amount(amount)
                .description("test")
                .build();
        assertThatThrownBy(() -> accountService.withdraw(command))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    @DisplayName("일일 한도 초과 시 출금 실패")
    void withdraw_exceed_daily_limit() {
        // given
        Long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1000);
        AccountEntity account = new AccountEntity("123", BigDecimal.valueOf(2000000));
        com.practice.storage.db.core.limit.DailyLimitUsageEntity dailyLimitUsage = new com.practice.storage.db.core.limit.DailyLimitUsageEntity(
                accountId, java.time.LocalDate.now());
        dailyLimitUsage.addWithdrawAmount(BigDecimal.valueOf(1000000)); // 이미 한도 도달

        given(accountReader.readWithLock(accountId)).willReturn(account);
        willDoNothing().given(dailyLimitUsageService).ensureDailyLimitUsageExists(any(), any());
        given(dailyLimitUsageReader.findByAccountIdAndDateWithLock(any(), any()))
                .willReturn(java.util.Optional.of(dailyLimitUsage));

        // when & then
        AccountWithdraw command = AccountWithdraw.builder()
                .accountId(accountId)
                .amount(amount)
                .description("test")
                .build();
        assertThatThrownBy(() -> accountService.withdraw(command))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.EXCEED_DAILY_WITHDRAW_LIMIT.getMessage());
    }

    @Test
    @DisplayName("계좌 이체 성공")
    void transfer_success() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String receiverAccountNumber = "RECEIVER-123";
        BigDecimal amount = BigDecimal.valueOf(10000); // 10,000 KRW
        BigDecimal fee = BigDecimal.valueOf(100); // 1% = 100 KRW
        BigDecimal totalAmount = amount.add(fee); // 10,100 KRW

        AccountEntity sender = new AccountEntity("SENDER", BigDecimal.valueOf(20000));
        sender.setId(senderId);
        AccountEntity receiver = new AccountEntity(receiverAccountNumber, BigDecimal.ZERO);
        receiver.setId(receiverId);

        com.practice.storage.db.core.limit.DailyLimitUsageEntity dailyLimitUsage = new com.practice.storage.db.core.limit.DailyLimitUsageEntity(
                senderId, java.time.LocalDate.now());

        given(accountReader.readIdByAccountNumber(receiverAccountNumber)).willReturn(receiverId);
        given(accountReader.readWithLock(senderId)).willReturn(sender);
        given(accountReader.readWithLock(receiverId)).willReturn(receiver);
        willDoNothing().given(dailyLimitUsageService).ensureDailyLimitUsageExists(any(), any());
        given(dailyLimitUsageReader.findByAccountIdAndDateWithLock(any(), any()))
                .willReturn(java.util.Optional.of(dailyLimitUsage));

        // when
        AccountTransfer transfer = AccountTransfer.builder()
                .senderAccountId(senderId)
                .receiverAccountNumber(receiverAccountNumber)
                .amount(amount)
                .description("Transfer")
                .build();
        accountService.transfer(transfer);

        // then
        assertThat(sender.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(9900)); // 20000 - 10100
        assertThat(receiver.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10000)); // 0 + 10000
        assertThat(dailyLimitUsage.getTotalTransferAmount()).isEqualByComparingTo(amount);
        verify(transactionWriter, times(2)).save(any());
    }

    @Test
    @DisplayName("계좌 이체 실패 - 잔액 부족 (수수료 포함)")
    void transfer_insufficient_balance() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String receiverAccountNumber = "RECEIVER-123";
        BigDecimal amount = BigDecimal.valueOf(10000);
        BigDecimal fee = BigDecimal.valueOf(100);

        AccountEntity sender = new AccountEntity("SENDER", BigDecimal.valueOf(10000)); // 잔액 10,000 (수수료 부족)
        sender.setId(senderId);
        AccountEntity receiver = new AccountEntity(receiverAccountNumber, BigDecimal.ZERO);
        receiver.setId(receiverId);

        com.practice.storage.db.core.limit.DailyLimitUsageEntity dailyLimitUsage = new com.practice.storage.db.core.limit.DailyLimitUsageEntity(
                senderId, java.time.LocalDate.now());

        given(accountReader.readIdByAccountNumber(receiverAccountNumber)).willReturn(receiverId);
        given(accountReader.readWithLock(senderId)).willReturn(sender);
        given(accountReader.readWithLock(receiverId)).willReturn(receiver);
        willDoNothing().given(dailyLimitUsageService).ensureDailyLimitUsageExists(any(), any());
        given(dailyLimitUsageReader.findByAccountIdAndDateWithLock(any(), any()))
                .willReturn(java.util.Optional.of(dailyLimitUsage));

        // when & then
        AccountTransfer transfer = AccountTransfer.builder()
                .senderAccountId(senderId)
                .receiverAccountNumber(receiverAccountNumber)
                .amount(amount)
                .description("Transfer")
                .build();

        assertThatThrownBy(() -> accountService.transfer(transfer))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    @DisplayName("계좌 이체 실패 - 일일 이체 한도 초과")
    void transfer_exceed_daily_limit() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String receiverAccountNumber = "RECEIVER-123";
        BigDecimal amount = BigDecimal.valueOf(3_000_001); // 한도 초과

        AccountEntity sender = new AccountEntity("SENDER", BigDecimal.valueOf(5000000));
        sender.setId(senderId);
        AccountEntity receiver = new AccountEntity(receiverAccountNumber, BigDecimal.ZERO);
        receiver.setId(receiverId);

        com.practice.storage.db.core.limit.DailyLimitUsageEntity dailyLimitUsage = new com.practice.storage.db.core.limit.DailyLimitUsageEntity(
                senderId, java.time.LocalDate.now());

        given(accountReader.readIdByAccountNumber(receiverAccountNumber)).willReturn(receiverId);
        given(accountReader.readWithLock(senderId)).willReturn(sender);
        given(accountReader.readWithLock(receiverId)).willReturn(receiver);
        willDoNothing().given(dailyLimitUsageService).ensureDailyLimitUsageExists(any(), any());
        given(dailyLimitUsageReader.findByAccountIdAndDateWithLock(any(), any()))
                .willReturn(java.util.Optional.of(dailyLimitUsage));

        // when & then
        AccountTransfer transfer = AccountTransfer.builder()
                .senderAccountId(senderId)
                .receiverAccountNumber(receiverAccountNumber)
                .amount(amount)
                .description("Transfer")
                .build();

        assertThatThrownBy(() -> accountService.transfer(transfer))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.EXCEED_DAILY_TRANSFER_LIMIT.getMessage());
    }
}
