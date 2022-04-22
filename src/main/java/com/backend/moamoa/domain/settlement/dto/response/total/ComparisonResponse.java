package com.backend.moamoa.domain.settlement.dto.response.total;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ComparisonResponse {

    private String content;

    private int previous;

    private int present;

    private int difference;

    private int ratio;

    @Builder
    public ComparisonResponse(String content, int prev, int present, int difference, int ratio) {
        this.content = content;
        this.previous = prev;
        this.present = present;
        this.difference = difference;
        this.ratio = ratio;
    }

}
