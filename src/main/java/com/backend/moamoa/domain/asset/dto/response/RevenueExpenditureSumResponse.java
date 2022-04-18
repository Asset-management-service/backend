package com.backend.moamoa.domain.asset.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
public class RevenueExpenditureSumResponse {

    private int totalRevenue;

    private int totalExpenditure;

    private int remainingBudget;

    private Page<RevenueExpenditureResponse> revenueExpenditureResponses;

}
