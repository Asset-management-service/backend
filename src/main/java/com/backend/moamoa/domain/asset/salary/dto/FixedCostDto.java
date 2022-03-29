package com.backend.moamoa.domain.asset.salary.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FixedCostDto {

    private Map<String, Integer> category;

    private int totalCost;

}
