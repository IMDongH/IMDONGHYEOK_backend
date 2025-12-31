package com.practice.storage.db.core.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    boolean existsByAccountNumber(String accountNumber);

    java.util.Optional<AccountEntity> findByIdAndStatus(Long id, com.practice.core.enums.EntityStatus status);
}
