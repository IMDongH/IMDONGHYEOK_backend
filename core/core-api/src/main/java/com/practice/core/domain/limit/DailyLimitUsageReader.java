package com.practice.core.domain.limit;

import com.practice.storage.db.core.limit.DailyLimitUsageEntity;
import com.practice.storage.db.core.limit.DailyLimitUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DailyLimitUsageReader {
    private final DailyLimitUsageRepository dailyLimitUsageRepository;

    public Optional<DailyLimitUsageEntity> findByAccountIdAndDate(Long accountId, LocalDate date) {
        return dailyLimitUsageRepository.findByAccountIdAndDate(accountId, date);
    }

    public Optional<DailyLimitUsageEntity> findByAccountIdAndDateWithLock(Long accountId, LocalDate date) {
        return dailyLimitUsageRepository.findByAccountIdAndDateWithPessimisticLock(accountId, date);
    }
}
