package com.backend.moamoa.domain.asset.dto.request;

import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditureType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@ApiModel(description = "수익 지출 수정 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class UpdateRevenueExpenditure {

    @ApiModelProperty(value = "수익 지출 PK", example = "1", required = true)
    private Long revenueExpenditureId;

    @ApiModelProperty(value = "수익, 지출 타입", example = "REVENUE", required = true)
    private RevenueExpenditureType revenueExpenditureType;

    @ApiModelProperty(value = "해당 카테고리 타입", example = "FIXED", required = true)
    private AssetCategoryType assetCategoryType;

    @ApiModelProperty(value = "해당 년월일", example = "2022-04-19", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @ApiModelProperty(value = "카테고리 이름", example = "월급", required = true)
    private String categoryName;

    @ApiModelProperty(value = "결제 수단", example = "신용 카드")
    private String paymentMethod;

    @ApiModelProperty(value = "금액", example = "6500", required = true)
    private int cost;

    @ApiModelProperty(value = "내용", example = "신전 떡볶이")
    private String content;
}
