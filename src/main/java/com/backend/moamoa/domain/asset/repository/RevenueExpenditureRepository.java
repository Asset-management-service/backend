package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RevenueExpenditureRepository extends JpaRepository<RevenueExpenditure, Long>, RevenueExpenditureRepositoryCustom{
    @Query("SELECT sum(r.cost) from RevenueExpenditure r where (r.date between :start and :end) " +
            "and r.revenueExpenditureType = com.backend.moamoa.domain.asset.entity.RevenueExpenditureType.EXPENDITURE " +
            "and r.user = :user")
    Integer getExpenditure(LocalDate start, LocalDate end, User user);

    @Query("SELECT sum(r.cost) from RevenueExpenditure r where (r.date between :start and :end) " +
            "and r.revenueExpenditureType = com.backend.moamoa.domain.asset.entity.RevenueExpenditureType.REVENUE " +
            "and r.user = :user")
    Integer getRevenue(LocalDate start, LocalDate end, User user);

}
