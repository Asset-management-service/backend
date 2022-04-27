package com.backend.moamoa.domain.settlement.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalResponse {

    int totalExp;

    int totalRevenue;

    int totalFixed;

    int totalVariable;

    public void setTotalResponse(String revenueExpenditureType, int cost) {
        if (revenueExpenditureType.equals("REVENUE")) {
            this.totalRevenue += cost;
        }
        if (revenueExpenditureType.equals("EXPENDITURE")) {
            this.totalExp += cost;
        }
    }

    public void addTotalFixed(int totalFixed) {
        this.totalFixed += totalFixed;
    }

    public void addTotalVariable(int totalVariable) {
        this.totalVariable += totalVariable;
    }

}
