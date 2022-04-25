package com.backend.moamoa.domain.asset.dto.response;

import com.backend.moamoa.domain.asset.entity.RevenueExpenditureType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RevenueExpenditureResponse {

    @ApiModelProperty(value = "수익 지출 PK")
    private Long revenueExpenditureId;

    @ApiModelProperty(value = "수익 지출 타입")
    private RevenueExpenditureType revenueExpenditureType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @ApiModelProperty(value = "카테고리 이름")
    private String categoryName;

    @ApiModelProperty(value = "카테고리 내용")
    private String content;

    @ApiModelProperty(value = "결제 수단")
    private String paymentMethod;

    @ApiModelProperty(value = "수익 OR 지출")
    private int cost;

    @QueryProjection
    public RevenueExpenditureResponse(Long revenueExpenditureId, RevenueExpenditureType revenueExpenditureType, LocalDate date, String categoryName, String content, String paymentMethod, int cost) {
        this.revenueExpenditureId = revenueExpenditureId;
        this.revenueExpenditureType = revenueExpenditureType;
        this.date = date;
        this.categoryName = categoryName;
        this.content = content;
        this.paymentMethod = paymentMethod;
        this.cost = cost;
    }
}
