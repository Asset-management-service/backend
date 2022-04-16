package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.entity.QAssetCategory;
import com.backend.moamoa.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.backend.moamoa.domain.asset.entity.QAssetCategory.*;
import static com.backend.moamoa.domain.user.entity.QUser.*;

@RequiredArgsConstructor
public class AssetCategoryRepositoryImpl implements AssetCategoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<String> findByAssetCategoryTypeAndUserId(String assetCategoryType, Long userId) {

        return queryFactory
                .select(assetCategory.categoryName)
                .from(assetCategory)
                .innerJoin(assetCategory.user, user)
                .where(assetCategory.assetCategoryType.eq(AssetCategoryType.valueOf(assetCategoryType.toUpperCase())).and(user.id.eq(userId)))
                .fetch();
    }
}