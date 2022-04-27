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
public class BudgetResponse {

    @ApiModelProperty(value = "한달 예산 금액 PK")
    private Long budgetId;

    @ApiModelProperty(value = "한달 예산 금액")
    private int budgetAmount;

}
