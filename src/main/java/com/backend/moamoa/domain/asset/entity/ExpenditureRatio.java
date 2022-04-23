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
public class ExpenditureRatio {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "expenditure_ratio_id")
    private Long id;

    @Column(nullable = false)
    private int fixed;

    @Column(nullable = false)
    private int variable;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ExpenditureRatio(int fixed, int variable, User user) {
        this.fixed = fixed;
        this.variable = variable;
        this.user = user;
    }

    public static ExpenditureRatio createExpenditureRatio(int fixed, int variable, User user) {
        return ExpenditureRatio.builder()
                .fixed(fixed)
                .variable(variable)
                .user(user)
                .build();
    }

    public void updateExpenditureRatio(int variable, int fixed) {
        this.variable = variable;
        this.fixed = fixed;
    }
}
