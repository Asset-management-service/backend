package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface RevenueExpenditureRepositoryCustom {
    Page<RevenueExpenditureResponse> findRevenueAndExpenditureByMonth(LocalDate month, Pageable pageable, Long userId);
}
