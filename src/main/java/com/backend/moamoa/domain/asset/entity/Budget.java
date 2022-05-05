package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Budget {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @Column(nullable = false)
    private int budgetAmount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Budget(Long id, int budgetAmount, User user) {
        this.id = id;
        this.budgetAmount = budgetAmount;
        this.user = user;
    }

    public void updateBudgetAmount(int budgetAmount){
        this.budgetAmount = budgetAmount;
    }

    public static Budget createBudget(int budgetAmount, User user) {
        return Budget.builder()
                .budgetAmount(budgetAmount)
                .user(user)
                .build();
    }
}
