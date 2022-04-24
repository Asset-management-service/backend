package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "머니 로그 수익 지출 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyLogRevenueExpenditureResponse {

    @ApiModelProperty(value = "총 수익")
    private int totalRevenue;

    @ApiModelProperty(value = "총 지출")
    private int totalExpenditure;

    @ApiModelProperty(value = "남은 예산")
    private int totalRevenueExpenditure;

    @ApiModelProperty(value = "수익 지출 내역")
    private List<RevenueExpenditureResponse> revenueExpenditureResponses = new ArrayList<>();

}
