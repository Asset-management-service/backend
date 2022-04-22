package com.backend.moamoa.domain.settlement.dto.response.settle;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;

@ApiModel(description = "주별 결산 응답 모델")
@Getter
@Setter
@NoArgsConstructor
public class WeekSettleResponse {

    @ApiModelProperty(value = "해당 년", example = "2022")
    private Integer year;

    @ApiModelProperty(value = "해당 달", example = "3")
    private Integer month;

    @ApiModelProperty(value = "해당 주차", example = "4")
    private Integer dayOfWeek;

    @ApiModelProperty(value = "지출 내역", example = "15000")
    private Integer cost;

    @ApiModelProperty(value = "시작 년 월 일", example = "15000")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate weekStart;

    @ApiModelProperty(value = "끝 년 월 일", example = "15000")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate weekEnd;

    @Builder
    public WeekSettleResponse(int year, int month, int dayOfWeek, Integer cost, LocalDate weekStart, LocalDate weekEnd) {
        this.year = year;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
        this.cost = (cost == null) ? 0 : cost;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
    }

}
