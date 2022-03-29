package com.backend.moamoa.domain.asset.salary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRequest {


    private List<FixedCostDto> fixed;

//    private CostType costType;

    private List<VariableCostDto> variable;

    private int expenditure;


//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDateTime date;

}
