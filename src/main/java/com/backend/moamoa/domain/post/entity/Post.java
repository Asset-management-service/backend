package com.backend.moamoa.domain.post.entity;

import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EntityListeners(AuditListener.class)
public class Post implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Embedded
    private TimeEntity timeEntity;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_category_id")
    private PostCategory postCategory;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    @Builder
    public Post(String title, String content, Integer viewCount, User user, List<Comment> comments) {
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.user = user;
        this.comments = comments;
    }

    /**
     * 생성 메서드
     */
    public static Post createPost(String title, String content, User user){
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
