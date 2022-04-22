package com.backend.moamoa.domain.asset.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RevenueExpenditureResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    private String categoryName;

    private String content;

    private String paymentMethod;

    private int cost;

    @QueryProjection
    public RevenueExpenditureResponse(LocalDate date, String categoryName, String content, String paymentMethod, int cost) {
        this.date = date;
        this.categoryName = categoryName;
        this.content = content;
        this.paymentMethod = paymentMethod;
        this.cost = cost;
    }
}
