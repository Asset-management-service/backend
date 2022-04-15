package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {

    List<AssetCategory> findByCategoryNameAndUserId(String categoryName, Long userId);
}
