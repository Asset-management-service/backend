package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@RequiredArgsConstructor
public class RevenueExpenditureRepositoryImpl implements RevenueExpenditureRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RevenueExpenditure> findRevenueAndExpenditureByMonth(LocalDate month, Pageable pageable) {
        return null;
    }
}
