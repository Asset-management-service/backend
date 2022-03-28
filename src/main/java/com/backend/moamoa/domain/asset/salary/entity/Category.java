package com.backend.moamoa.domain.asset.salary.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "asset_category_id")
    private Long id;

    private String category;
    private int cost;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "salary_id")
    private Salary salary;


    @Builder
    public Category(String category, int cost) {
        this.category = category;
        this.cost = cost;
    }
}
