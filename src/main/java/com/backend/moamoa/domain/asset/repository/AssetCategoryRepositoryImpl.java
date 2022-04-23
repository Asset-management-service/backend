package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.backend.moamoa.domain.asset.entity.QAssetCategory.assetCategory;
import static com.backend.moamoa.domain.user.entity.QUser.user;

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

    @Override
    public List<String> findByTwoAssetCategoriesAndUserId(String firstType, String secondType, Long userId) {
        return queryFactory
                .select(assetCategory.categoryName)
                .from(assetCategory)
                .innerJoin(assetCategory.user, user)
                .where((assetCategory.assetCategoryType.eq(AssetCategoryType.valueOf(firstType.toUpperCase()))
                        .or(assetCategory.assetCategoryType.eq(AssetCategoryType.valueOf(secondType.toUpperCase()))))
                        .and(user.id.eq(userId)))
                .fetch();
    }
}
