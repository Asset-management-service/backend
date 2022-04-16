package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface RevenueExpenditureRepositoryCustom {
    Page<RevenueExpenditure> findRevenueAndExpenditureByMonth(LocalDate month, Pageable pageable);
}
