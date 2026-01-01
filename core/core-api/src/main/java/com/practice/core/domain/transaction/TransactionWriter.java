package com.practice.core.domain.transaction;

import com.practice.storage.db.core.transaction.TransactionEntity;
import com.practice.storage.db.core.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionWriter {
    private final TransactionRepository transactionRepository;

    public TransactionEntity save(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }
}
