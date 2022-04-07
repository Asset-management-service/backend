package com.backend.moamoa.domain.asset.salary.repository;

import com.backend.moamoa.domain.asset.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
}
