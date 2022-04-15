package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
