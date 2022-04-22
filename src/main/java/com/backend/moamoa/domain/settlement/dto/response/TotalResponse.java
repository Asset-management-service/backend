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

    public void setTotalResponse(String revenueExpenditureType, String categoryName, int cost) {
        if (revenueExpenditureType.equals("REVENUE")) {
            this.totalRevenue += cost;
        }
        if (revenueExpenditureType.equals("EXPENDITURE")) {
            this.totalExp += cost;
        }
        if (categoryName.equals("FIXED")) {
            this.totalFixed += cost;
        }
        if (categoryName.equals("VARIABLE")) {
            this.totalVariable += cost;
        }
    }

}
