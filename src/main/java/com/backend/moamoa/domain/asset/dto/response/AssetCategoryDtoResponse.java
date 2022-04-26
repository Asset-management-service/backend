package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetCategoryDtoResponse {

    @ApiModelProperty(value = "카테고리 리스트")
    private List<AssetCategoriesResponse> categories = new ArrayList<>();

}
