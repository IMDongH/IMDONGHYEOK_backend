package com.practice.core.domain.account;

import com.practice.storage.db.core.account.AccountEntity;
import com.practice.storage.db.core.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat; // Used in other tests if added, but currently unused. Wait, I should just remove it if unused.
// Actually, I'll just remove the specific unused lines.

@SpringBootTest
@Transactional
class AccountReaderTest {

    @Autowired
    private AccountReader accountReader;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("삭제된 계좌 조회 시 조회되지 않아야 한다")
    void read_deletedAccount() {
        // given
        AccountEntity account = new AccountEntity("TEST-ACCOUNT-" + System.currentTimeMillis(), BigDecimal.ZERO);
        account.delete(); // status = DELETED
        accountRepository.save(account);

        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> accountReader.read(account.getId()))
                .isInstanceOf(com.practice.core.support.error.CoreException.class)
                .hasMessage(com.practice.core.support.error.ErrorType.ACCOUNT_NOT_FOUND.getMessage());
    }
}
