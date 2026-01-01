package com.practice.core.domain.limit;

import com.practice.storage.db.core.limit.DailyLimitUsageEntity;
import com.practice.storage.db.core.limit.DailyLimitUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyLimitUsageWriter {
    private final DailyLimitUsageRepository dailyLimitUsageRepository;

    public DailyLimitUsageEntity save(DailyLimitUsageEntity dailyLimitUsageEntity) {
        return dailyLimitUsageRepository.save(dailyLimitUsageEntity);
    }
}
