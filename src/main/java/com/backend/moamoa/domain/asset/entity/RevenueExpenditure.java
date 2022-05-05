package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class RevenueExpenditure implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "revenue_expenditure_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(STRING)
    private RevenueExpenditureType revenueExpenditureType;

    @Enumerated(STRING)
    @Column(nullable = false)
    private AssetCategoryType assetCategoryType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String categoryName;

    private String paymentMethod;

    @Column(nullable = false)
    private int cost;

    private String content;

    @Embedded
    private TimeEntity timeEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RevenueExpenditure(Long id, RevenueExpenditureType revenueExpenditureType, AssetCategoryType assetCategoryType, LocalDate date, String categoryName, int cost, String content, User user, String paymentMethod) {
        this.id = id;
        this.revenueExpenditureType = revenueExpenditureType;
        this.assetCategoryType = assetCategoryType;
        this.date = date;
        this.categoryName = categoryName;
        this.cost = cost;
        this.user = user;
        this.paymentMethod = paymentMethod;
        this.content = content;
    }

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    public void updateRevenueExpenditure(RevenueExpenditureType revenueExpenditureType, AssetCategoryType assetCategoryType, String content, LocalDate date, String paymentMethod, String categoryName, int cost) {
        this.revenueExpenditureType = revenueExpenditureType;
        this.assetCategoryType = assetCategoryType;
        this.content = content;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.categoryName = categoryName;
        this.cost = cost;
    }
}
