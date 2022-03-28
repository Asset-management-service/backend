package com.backend.moamoa.domain.asset.salary.entity;

import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "salary")
    private List<Category> categories = new ArrayList<>();


    private int totalExpenditure;

    @Builder
    public Salary(List<Category> category, int totalExpenditure) {
        category.stream()
                .forEach(assetCategory -> this.categories.add(assetCategory));
        this.totalExpenditure = totalExpenditure;
    }

    @Embedded
    private TimeEntity timeEntity;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }
}
