package com.backend.moamoa.domain.asset.salary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VariableCostDto {

    private List<String> variableCostCategories;

    private int cost;

}
