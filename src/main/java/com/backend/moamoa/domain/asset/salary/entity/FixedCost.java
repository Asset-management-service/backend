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
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class FixedCost {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "fixed_cost_id")
    private Long id;

    private List<String> fixedCostCategories = new ArrayList<>();

    private int cost;

    public FixedCost(List<String> fixedCostCategories, int cost) {
        fixedCostCategories.stream()
                .forEach(category -> this.fixedCostCategories.add(category));
        this.cost = cost;
    }

    public static FixedCost createFixedCost(List<String> fixedCostCategories, int cost) {
        return new FixedCost(fixedCostCategories, cost);
    }
}
