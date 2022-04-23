package com.backend.moamoa.domain.settlement.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel(description = "달 별 결산 응답 모델")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class MonthResponse
{

    @ApiModelProperty(value = "최대 지출 카테고리", example = "식비")
    private String mostExpCategory;

    @ApiModelProperty(value = "최소 지출 카테고리", example = "기타")
    private String leastExpCategory;

    @ApiModelProperty(value = "총 지출 내역", example = "15000")
    private int totalExp;

    @ApiModelProperty(value = "총 수익 내역", example = "15000")
    private int totalRevenue;

    private List<CostResponse> fixedCostResponses;

    private List<CostResponse> variableCostResponses;

    private List<RevenueResponse> revenueResponses;

    @ApiModelProperty(value = "총 순이익", example = "15000")
    private Integer netIncome;

    @ApiModelProperty(value = "고정 비율 초과 여부", example = "true")
    private Boolean fixedExceed;

    @ApiModelProperty(value = "변동 비율 초과 여부", example = "true")
    private Boolean variableExceed;

    public MonthResponse() {
        this.fixedExceed = false;
        this.variableExceed = false;
    }

}
