package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.ExpenditureRatio;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenditureRatioRepository extends JpaRepository<ExpenditureRatio, Long> {

    Optional<ExpenditureRatio> findByUser(User user);

}
