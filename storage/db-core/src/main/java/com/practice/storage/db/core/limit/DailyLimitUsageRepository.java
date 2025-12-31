package com.practice.storage.db.core.limit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyLimitUsageRepository extends JpaRepository<DailyLimitUsageEntity, Long> {
    Optional<DailyLimitUsageEntity> findByAccountIdAndDate(Long accountId, LocalDate date);
}
