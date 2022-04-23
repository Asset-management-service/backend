package com.backend.moamoa.domain.settlement.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ApiModel(description = "고정 비용 응답 모델")
public class CostResponse {

    @ApiModelProperty(value = "총 비용", example = "15000")
    private int totalCost;

    @ApiModelProperty(value = "총 퍼센트", example = "84")
    private int totalPercent;

    @ApiModelProperty(value = "비용 카테고리", example = "통신비")
    private String CategoryName;

    @ApiModelProperty(value = "지출 비용", example = "15000")
    private int cost;

    @ApiModelProperty(value = "퍼센트", example = "34")
    private int percent;

    @Builder
    public CostResponse(int totalCost, int totalPercent, String categoryName, int cost, int percent) {
        this.totalCost = totalCost;
        this.totalPercent = totalPercent;
        this.CategoryName = categoryName;
        this.cost = cost;
        this.percent = percent;
    }
}
