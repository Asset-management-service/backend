package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueExpenditureSumResponse {

    @ApiModelProperty(value = "한달 수익")
    private int totalRevenue;

    @ApiModelProperty(value = "한달 지출")
    private int totalExpenditure;

    @ApiModelProperty(value = "이번달 남은 예산")
    private int remainingBudget;

    @ApiModelProperty(value = "수익 지출 내역")
    private Page<RevenueExpenditureResponse> revenueExpenditureResponses;


    public static RevenueExpenditureSumResponse of(int totalRevenue, int totalExpenditure, int remainingBudget, Page<RevenueExpenditureResponse> revenueExpenditureResponses) {
        return new RevenueExpenditureSumResponse(totalRevenue, totalExpenditure, remainingBudget, revenueExpenditureResponses);
    }
}
