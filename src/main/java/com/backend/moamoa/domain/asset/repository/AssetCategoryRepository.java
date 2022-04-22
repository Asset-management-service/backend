package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long>, AssetCategoryRepositoryCustom {

    Optional<AssetCategory> findByIdAndUserId(Long categoryId, Long id);

    Optional<AssetCategory> findByCategoryNameAndUserId(String categoryName, Long id);

    List<AssetCategory> findByUser(User user);

}
