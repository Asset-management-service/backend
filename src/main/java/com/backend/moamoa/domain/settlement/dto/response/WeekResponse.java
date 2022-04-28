package com.backend.moamoa.domain.settlement.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel(description = "주 별 결산 응답 모델")
@Getter
@Setter
@NoArgsConstructor
public class WeekResponse
{

    @ApiModelProperty(value = "해당 날짜의 년", example = "2022")
    private Integer year;

    @ApiModelProperty(value = "해당 날짜의 주", example = "3")
    private int month;

    @ApiModelProperty(value = "해당 날짜의 주차", example = "3")
    private Integer weekOfMonth;

    @ApiModelProperty(value = "최대 지출 카테고리", example = "식비")
    private String mostExpCategory;

    @ApiModelProperty(value = "최소 지출 카테고리", example = "기타")
    private String leastExpCategory;

    @ApiModelProperty(value = "총 지출 내역", example = "15000")
    private int totalExp;

    private List<CostResponse> costResponses;

    @Builder
    public WeekResponse(Integer year, Integer month, Integer weekOfMonth, String mostExpCategory, String leastExpCategory, int totalExp, List<CostResponse> fixedCostResponses) {
        this.year = year;
        this.month = month;
        this.weekOfMonth = weekOfMonth;
        this.mostExpCategory = mostExpCategory;
        this.leastExpCategory = leastExpCategory;
        this.totalExp = totalExp;
        this.costResponses = fixedCostResponses;
    }

}
