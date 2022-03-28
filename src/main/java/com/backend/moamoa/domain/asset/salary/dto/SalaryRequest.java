package com.backend.moamoa.domain.asset.salary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class SalaryRequest {

    private List<FixedCostDto> fixed;

    private List<VariableCostDto> variable;

    private int expenditure;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date;

}
