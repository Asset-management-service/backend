package com.backend.moamoa.domain.settlement.dto.response.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "년별 결산 응답 모델")
@Getter
@Setter
@NoArgsConstructor
public class YearSettleResponse {

    @ApiModelProperty(value = "해당 년", example = "2022")
    private Integer year;

    @ApiModelProperty(value = "지출 내역", example = "15000")
    private Integer cost;

    @ApiModelProperty(value = "수익 내역", example = "15000")
    private Integer revenue;

    @Builder
    public YearSettleResponse(int year, Integer cost, Integer revenue) {
        this.year = year;
        this.cost = (cost == null) ? 0 : cost;
        this.revenue = (revenue == null) ? 0 : revenue;
    }

}
