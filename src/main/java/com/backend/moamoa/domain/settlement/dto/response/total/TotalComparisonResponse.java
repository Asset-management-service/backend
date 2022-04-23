package com.backend.moamoa.domain.settlement.dto.response.total;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalComparisonResponse {

    private ComparisonResponse categoryType;

    private ComparisonResponse categoryName;

    @Builder
    public TotalComparisonResponse(ComparisonResponse categoryType, ComparisonResponse categoryName) {
        this.categoryType = categoryType;
        this.categoryName = categoryName;
    }
}
