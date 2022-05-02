package com.backend.moamoa.domain.asset.dto.response;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "결과 응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetCategoriesResponse {

    @ApiModelProperty(value = "카테고리 PK")
    private Long categoryId;

    @ApiModelProperty(value = "카테고리 이름")
    private String categoryName;

    public AssetCategoriesResponse(AssetCategory assetCategory) {
        this.categoryId = assetCategory.getId();
        this.categoryName = assetCategory.getCategoryName();
    }

}
