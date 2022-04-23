package com.backend.moamoa.domain.settlement.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ApiModel(description = "수익 응답 모델")
public class RevenueResponse {

    @ApiModelProperty(value = "수익 카테고리", example = "월급")
    private String CategoryName;

    @ApiModelProperty(value = "수익 비용", example = "15000")
    private int revenue;

    @ApiModelProperty(value = "수익 퍼센트", example = "34")
    private int percent;

}
