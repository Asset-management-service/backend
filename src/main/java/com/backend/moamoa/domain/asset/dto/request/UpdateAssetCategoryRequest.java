package com.backend.moamoa.domain.asset.dto.request;

import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel(description = "카테고리 수정 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetCategoryRequest {

    @NotNull
    @ApiModelProperty(value = "카테고리 PK", example = "1", required = true)
    private Long categoryId;

    @NotNull
    @ApiModelProperty(value = "카테고리 타입", example = "VARIABLE", required = true)
    private AssetCategoryType categoryType;

    @NotNull
    @ApiModelProperty(value = "카테고리 이름", example = "월급", required = true)
    private String categoryName;

}
