package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.MoneyLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyLogRepository extends JpaRepository<MoneyLog, Long> {
}
