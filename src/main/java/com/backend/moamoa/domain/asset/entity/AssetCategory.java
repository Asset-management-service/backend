package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private AssetCategoryType assetCategoryType;

    private String categoryName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public AssetCategory(AssetCategoryType assetCategoryType, String categoryName, Asset asset, User user) {
        this.assetCategoryType = assetCategoryType;
        this.categoryName = categoryName;
        this.asset = asset;
        this.user = user;
    }
}
