package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetCategory;

import java.util.List;

public interface AssetCategoryRepositoryCustom {

    List<AssetCategory> findByAssetCategoryTypeAndUserId(String assetCategoryType, Long userId);

    List<String> findByTwoAssetCategoriesAndUserId(String firstType, String secondType, Long userId);

}
