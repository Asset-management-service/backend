package com.backend.moamoa.domain.asset.dto.request;

import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import io.swagger.annotations.ApiModel;
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
    private Long categoryId;

    @NotNull
    private AssetCategoryType categoryType;

    @NotNull
    private String categoryName;

}
