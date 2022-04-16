package com.backend.moamoa.domain.asset.dto.response;

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

}
