package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.entity.Asset;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.asset.repository.AssetRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final AssetCategoryRepository assetCategoryRepository;

    @Transactional
    public Long addCategory(AssetCategoryRequest request) {
        User user = userRepository.findById(1L).get();
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ASSET));

        AssetCategory category = AssetCategory.builder()
                .assetCategoryType(request.getCategoryType())
                .categoryName(request.getCategoryName())
                .user(user)
                .asset(asset)
                .build();
        assetCategoryRepository.save(category);

        return category.getId();
    }
}
