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

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccount() {
        // given
        String accountNumber = "123-456-789";
        AccountEntity savedAccount = new AccountEntity(accountNumber, BigDecimal.ZERO);

        given(accountReader.existsByAccountNumber(accountNumber)).willReturn(false);
        given(accountWriter.save(any(AccountEntity.class))).willReturn(savedAccount);

        // when
        Long accountId = accountService.createAccount(accountNumber);

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
        assertThatThrownBy(() -> accountService.createAccount(accountNumber))
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
}
