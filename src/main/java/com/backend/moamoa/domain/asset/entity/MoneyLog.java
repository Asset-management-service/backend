package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.post.entity.PostImage;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class MoneyLog implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "money_log_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "moneyLog", orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private TimeEntity timeEntity;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    @Builder
    public MoneyLog(LocalDate date, String content, List<PostImage> postImages, User user) {
        this.date = date;
        this.content = content;
        this.postImages = postImages;
        this.user = user;
    }

    public static MoneyLog createMoneyLog(LocalDate date, String content, User user) {
        return MoneyLog.builder()
                .date(date)
                .content(content)
                .user(user)
                .build();
    }
}
