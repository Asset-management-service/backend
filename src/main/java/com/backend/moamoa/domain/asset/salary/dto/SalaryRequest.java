package com.backend.moamoa.domain.asset.salary.dto;

import com.backend.moamoa.domain.asset.salary.entity.FixedCost;
import com.backend.moamoa.domain.asset.salary.entity.VariableCost;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalaryRequest {

    //고정비
    @NotNull
    private FixedCostDto fixedCosts;

    //변동비
    @NotNull
    private VariableCostDto variableCosts;

    //총 지출
    @NotNull
    private int totalExpenditure;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date;

}
