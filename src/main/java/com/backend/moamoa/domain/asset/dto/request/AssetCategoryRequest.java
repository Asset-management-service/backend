package com.backend.moamoa.domain.asset.dto.request;

import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssetCategoryRequest {

    private Long assetId;

    private AssetCategoryType categoryType;

    private String categoryName;
}
