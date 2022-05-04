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
public class CreateAssetGoalResponse {

    @ApiModelProperty(value = "자산 관리 목표 생성 ID")
    private Long assetGoalId;

}
