package com.backend.moamoa.domain.comment.entity;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class Comment implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String context;

    @Embedded
    private TimeEntity timeEntity;


    public Comment(String context) {
        this.context = context;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity =timeEntity;
    }
}
