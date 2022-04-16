package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long>, AssetCategoryRepositoryCustom {

    Optional<AssetCategory> findByIdAndUserId(Long categoryId, Long id);

}
