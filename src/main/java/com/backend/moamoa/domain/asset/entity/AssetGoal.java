package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDate;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class AssetGoal {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "asset_goal_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public AssetGoal(Long id, String content, User user, LocalDate date) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.date = date;
    }

    public static AssetGoal createAssetGoal(String content, User user, LocalDate date) {
        return AssetGoal.builder()
                .content(content)
                .user(user)
                .date(date)
                .build();
    }

    public void updateAssetGoal(String content) {
        this.content = content;
    }
}
