package com.backend.moamoa.domain.asset.salary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VariableCostDto {

    private List<Map<String, Integer>> category;

    private int totalCost;

}
