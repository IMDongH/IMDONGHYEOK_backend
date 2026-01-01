package com.practice.storage.db.core.account;

import com.practice.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    boolean existsByAccountNumber(String accountNumber);


    @Query(value = "SELECT * FROM account WHERE id = :id AND status = 'ACTIVE' FOR UPDATE", nativeQuery = true)
    Optional<AccountEntity> findByIdWithPessimisticLock(@Param("id") Long id);
}
