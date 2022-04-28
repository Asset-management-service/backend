package com.backend.moamoa.domain.settlement.dto.response.total;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ComparisonsResponse {

    private ComparisonResponse totalExpResponse;

    private ComparisonResponse totalFixedResponse;

    private List<ComparisonResponse> fixedResponses;

    private ComparisonResponse totalVarResponse;

    private List<ComparisonResponse> varResponses;

    private ComparisonResponse totalRevResponses;

    private List<ComparisonResponse> revResponses;

    private ComparisonResponse netProfitResponse;

    @Builder
    public ComparisonsResponse(ComparisonResponse totalExpResponse, ComparisonResponse totalFixedResponse, List<ComparisonResponse> fixedResponses, ComparisonResponse totalVarResponse, List<ComparisonResponse> varResponses, ComparisonResponse totalRevResponses, List<ComparisonResponse> revResponses, ComparisonResponse netProfitResponse) {
        this.totalExpResponse = totalExpResponse;
        this.totalFixedResponse = totalFixedResponse;
        this.fixedResponses = fixedResponses;
        this.totalVarResponse = totalVarResponse;
        this.varResponses = varResponses;
        this.totalRevResponses = totalRevResponses;
        this.revResponses = revResponses;
        this.netProfitResponse = netProfitResponse;
    }

}
