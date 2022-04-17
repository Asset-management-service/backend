package com.backend.moamoa.domain.asset.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueExpenditureResponse {

    private LocalDate date;

    private String categoryName;

    private String content;

    private String paymentMethod;

    private int cost;

    private int remainingBudget;

    private int revenue;

    private int expenditure;


    @QueryProjection
    public RevenueExpenditureResponse(LocalDate date, String categoryName, String content, String paymentMethod, int revenue, int expenditure) {
        this.date = date;
        this.categoryName = categoryName;
        this.content = content;
        this.paymentMethod = paymentMethod;
        this.revenue = revenue;
        this.expenditure = expenditure;
    }
}