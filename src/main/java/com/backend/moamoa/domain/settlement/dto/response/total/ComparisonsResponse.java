package com.backend.moamoa.domain.settlement.dto.response.total;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ComparisonsResponse {

    private ComparisonResponse totalExpResponse;

    private List<TotalComparisonResponse> fixedResponses;

    private List<TotalComparisonResponse> varianceResponses;

    private ComparisonResponse netProfitResponse;

    @Builder
    public ComparisonsResponse(ComparisonResponse totalExpResponse, List<TotalComparisonResponse> fixedResponses, List<TotalComparisonResponse> varianceResponses, ComparisonResponse netProfitResponse) {
        this.totalExpResponse = totalExpResponse;
        this.fixedResponses = fixedResponses;
        this.varianceResponses = varianceResponses;
        this.netProfitResponse = netProfitResponse;
    }

}
