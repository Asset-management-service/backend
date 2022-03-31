package com.backend.moamoa.domain.asset.salary.repository;

import com.backend.moamoa.domain.asset.salary.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
