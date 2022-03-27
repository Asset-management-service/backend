package com.backend.moamoa.domain.asset.salary.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor
@Entity
public class VariableCost {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "variable_cost_id")
    private Long id;

    private List<String> variableCostCategories = new ArrayList<>();

    private int cost;


    public VariableCost(List<String> variableCostCategories, int cost) {
        this.variableCostCategories = variableCostCategories;
        this.cost = cost;
    }

    public static VariableCost createVariableCost(List<String> variableCostCategories, int cost){
        return new VariableCost(variableCostCategories, cost);
    }
}
