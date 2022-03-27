package com.backend.moamoa.domain.asset.salary.entity;

import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class Salary implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "salary_id")
    private Long id;

    private int totalExpenditure;

    private LocalDateTime setDate;

    //고정비
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fixed_cost_id")
    private FixedCost fixedCost;

    //변동비
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "variable_cost_id")
    private VariableCost variableCost;


    @Embedded
    private TimeEntity timeEntity;


    @Builder
    public Salary(int totalExpenditure, LocalDateTime setDate, FixedCost fixedCost, VariableCost variableCost) {
        this.totalExpenditure = totalExpenditure;
        this.setDate = setDate;
        this.fixedCost = fixedCost;
        this.variableCost = variableCost;
    }

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }
}
