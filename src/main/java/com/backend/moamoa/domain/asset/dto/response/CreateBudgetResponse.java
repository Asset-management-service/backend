package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "결과 응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetResponse {

    @ApiModelProperty(value = "예산 금액 Id")
    private Long budgetId;

}
