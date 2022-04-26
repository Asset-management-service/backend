package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.MoneyLog;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MoneyLogRepository extends JpaRepository<MoneyLog, Long> {
    Optional<MoneyLog> findByUserAndId(User user, Long moneyLogId);

    Optional<MoneyLog> findByUserAndDate(User user, LocalDate date);
}
