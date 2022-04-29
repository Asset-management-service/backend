package com.backend.moamoa.domain.asset.dto.response;

import com.backend.moamoa.domain.asset.entity.ExpenditureRatio;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureResponse {

    @ApiModelProperty(value = "지출 비율 설정 PK")
    private Long expenditureRatioId;

    @ApiModelProperty(value = "고정비")
    private int fixed;

    @ApiModelProperty(value = "변동비")
    private int variable;

    public static ExpenditureResponse of(ExpenditureRatio expenditureRatio) {
        return new ExpenditureResponse(expenditureRatio.getId(), expenditureRatio.getFixed(), expenditureRatio.getVariable());
    }

}
