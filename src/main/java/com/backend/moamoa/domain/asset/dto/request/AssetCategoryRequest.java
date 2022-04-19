package com.backend.moamoa.domain.asset.dto.request;

import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "카테고리 생성 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class AssetCategoryRequest {

    @ApiModelProperty(value = "카테고리 타입", example = "REVENUE", required = true)
    private AssetCategoryType categoryType;

    @ApiModelProperty(value = "카테고리 이름", example = "월급", required = true)
    private String categoryName;

}
