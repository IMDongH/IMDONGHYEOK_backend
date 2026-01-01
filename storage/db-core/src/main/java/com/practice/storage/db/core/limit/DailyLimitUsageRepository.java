package com.practice.storage.db.core.limit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyLimitUsageRepository extends JpaRepository<DailyLimitUsageEntity, Long> {
    Optional<DailyLimitUsageEntity> findByAccountIdAndDate(Long accountId, LocalDate date);

    @Query(value = "SELECT * FROM daily_limit_usage WHERE account_id = :accountId AND date = :date FOR UPDATE", nativeQuery = true)
    Optional<DailyLimitUsageEntity> findByAccountIdAndDateWithPessimisticLock(
            @Param("accountId") Long accountId,
            @Param("date") LocalDate date);

    @Modifying
    @Query(value = "INSERT IGNORE INTO daily_limit_usage (account_id, date, total_transfer_amount, total_withdraw_amount, status, created_at, updated_at) VALUES (:accountId, :date, 0, 0, 'ACTIVE', NOW(), NOW())", nativeQuery = true)
    void insertIgnore(@Param("accountId") Long accountId, @Param("date") LocalDate date);
}
