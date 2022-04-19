package com.backend.moamoa.domain.asset.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "한달 예산 금액 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class BudgetRequest {

    @ApiModelProperty(value = "예산 금액", example = "1000000", required = true)
    private int budgetAmount;

}
