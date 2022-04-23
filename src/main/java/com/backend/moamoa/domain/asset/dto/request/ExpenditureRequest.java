package com.backend.moamoa.domain.asset.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel(description = "지출 비율 설정 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class ExpenditureRequest {

    @ApiModelProperty(value = "고정비", example = "40", required = true)
    @NotNull
    private int fixed;

    @NotNull
    @ApiModelProperty(value = "변동비", example = "60", required = true)
    private int variable;

}
