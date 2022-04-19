package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class AssetCategory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "asset_category_id")
    private Long id;

    @Enumerated(STRING)
    @NotNull
    @Column(name = "category_type")
    private AssetCategoryType assetCategoryType;

    @NotNull
    private String categoryName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Builder
    public AssetCategory(AssetCategoryType assetCategoryType, String categoryName, User user) {
        this.assetCategoryType = assetCategoryType;
        this.categoryName = categoryName;
        this.user = user;
    }

    public static AssetCategory createCategory(AssetCategoryType assetCategoryType, String categoryName, User user) {
        return AssetCategory.builder()
                .assetCategoryType(assetCategoryType)
                .categoryName(categoryName)
                .user(user)
                .build();
    }
}
