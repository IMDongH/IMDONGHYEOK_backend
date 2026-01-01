package com.practice.storage.db.core.transaction;

import com.practice.core.enums.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Slice<TransactionEntity> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    Slice<TransactionEntity> findByAccountIdAndTypeOrderByCreatedAtDesc(Long accountId,
            TransactionType type, Pageable pageable);
}
