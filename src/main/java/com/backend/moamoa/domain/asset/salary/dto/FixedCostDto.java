package com.backend.moamoa.domain.asset.salary.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FixedCostDto {

    private List<String> fixedCostCategories;

    private int cost;

}
