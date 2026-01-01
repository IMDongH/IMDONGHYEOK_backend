package com.practice.core.domain.limit;

import com.practice.storage.db.core.limit.DailyLimitUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyLimitUsageService {

    private final DailyLimitUsageRepository dailyLimitUsageRepository;

    @Transactional
    public void ensureDailyLimitUsageExists(Long accountId, LocalDate date) {
        dailyLimitUsageRepository.insertIgnore(accountId, date);
    }
}
