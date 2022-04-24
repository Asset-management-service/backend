package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetGoal;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AssetGoalRepository extends JpaRepository<AssetGoal, Long> {
    Optional<AssetGoal> findByUserAndDate(User user, LocalDate date);
}
