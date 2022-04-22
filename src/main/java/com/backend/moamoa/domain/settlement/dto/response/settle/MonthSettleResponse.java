package com.backend.moamoa.domain.settlement.dto.response.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(description = "달별 결산 응답 모델")
@Getter
@Setter
@NoArgsConstructor
public class MonthSettleResponse {

    @ApiModelProperty(value = "해당 달", example = "3")
    private int month;

    @ApiModelProperty(value = "해당 년", example = "2022")
    private int year;

    @ApiModelProperty(value = "지출 내역", example = "15000")
    private Integer cost;

    @ApiModelProperty(value = "수익 내역", example = "15000")
    private Integer revenue;

    @Builder
    public MonthSettleResponse(int month, int year, Integer cost, Integer revenue) {
        this.month = month;
        this.year = year;
        this.cost = (cost == null) ? 0 : cost;
        this.revenue = (revenue == null) ? 0 : revenue;
    }

}
