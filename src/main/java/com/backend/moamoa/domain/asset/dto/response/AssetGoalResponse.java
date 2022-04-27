package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetGoalResponse {

    @ApiModelProperty(value = "자산 관리 목표 PK")
    private Long assetGoalId;

    @ApiModelProperty(value = "자산 관리 목표 내용")
    private String content;

}
