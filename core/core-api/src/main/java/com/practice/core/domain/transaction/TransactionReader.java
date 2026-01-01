package com.practice.core.domain.transaction;

import com.practice.core.enums.TransactionType;
import com.practice.core.support.OffsetLimit;
import com.practice.core.support.Page;
import com.practice.storage.db.core.transaction.TransactionEntity;
import com.practice.storage.db.core.transaction.TransactionRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionReader {
    private final TransactionRepository transactionRepository;


    public Page<Transaction> readByAccountIdAndType(Long accountId, TransactionType type,
        OffsetLimit offsetLimit) {
        Slice<TransactionEntity> result;
        if(type==null){
             result = transactionRepository.findByAccountIdOrderByCreatedAtDesc(
                accountId, offsetLimit.toPageable());
        }else{
            result = transactionRepository.findByAccountIdAndTypeOrderByCreatedAtDesc(accountId, type, offsetLimit.toPageable());
        }

        List<Transaction> transactions = result.getContent().stream()
            .map(it -> Transaction.builder()
                .id(it.getId())
                .type(it.getType())
                .amount(it.getAmount())
                .balanceSnapshot(it.getBalanceSnapshot())
                .description(it.getDescription())
                .createdAt(it.getCreatedAt())
                .build())
            .toList();

        return new Page<>(transactions, result.hasNext());
    }
}
