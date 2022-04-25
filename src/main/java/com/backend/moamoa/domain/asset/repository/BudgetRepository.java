package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.Budget;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findBudgetAmountByUser(User user);
}
