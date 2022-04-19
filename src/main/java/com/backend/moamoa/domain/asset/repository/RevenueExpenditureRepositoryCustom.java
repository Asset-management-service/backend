package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RevenueExpenditureRepositoryCustom {

    Page<RevenueExpenditureResponse> findRevenueAndExpenditureByMonth(LocalDate month, Pageable pageable, Long userId);

    List<RevenueExpenditure> findRevenueExpenditure(LocalDate month, Long userId);

}
