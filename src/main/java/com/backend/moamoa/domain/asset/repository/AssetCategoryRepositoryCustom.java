package com.backend.moamoa.domain.asset.repository;

import java.util.List;

public interface AssetCategoryRepositoryCustom {

    List<String> findByAssetCategoryTypeAndUserId(String assetCategoryType, Long userId);

}
