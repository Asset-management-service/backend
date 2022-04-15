package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExpenditureRatio {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "expenditure_ratio_id")
    private Long id;

    @NotNull
    private int fixed;

    @NotNull
    private int variable;

    @ManyToOne(fetch = LAZY)
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
}
