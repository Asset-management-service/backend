package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueExpenditureRepository extends JpaRepository<RevenueExpenditure, Long>, RevenueExpenditureRepositoryCustom{

}
