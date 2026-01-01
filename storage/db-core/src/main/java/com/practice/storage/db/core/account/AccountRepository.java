package com.practice.storage.db.core.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    
    @Query("SELECT a.id FROM AccountEntity a WHERE a.accountNumber = :accountNumber")
    Optional<Long> findIdByAccountNumber(@Param("accountNumber") String accountNumber);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.id = :id AND a.status = 'ACTIVE'")
    Optional<AccountEntity> findByIdWithPessimisticLock(@Param("id") Long id);
}
