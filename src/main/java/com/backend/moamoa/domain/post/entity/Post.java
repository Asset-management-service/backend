package com.backend.moamoa.domain.post.entity;

import com.backend.moamoa.domain.user.entity.User;
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
    @Column(name = "view_count",nullable = false)
    private Integer viewCount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_category_id")
    private PostCategory postCategory;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Scrap> scraps = new ArrayList<>();


    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    @Builder
    public Post(String title, String content, Integer viewCount, User user, List<Comment> comments, PostCategory postCategory) {
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.user = user;
        this.comments = comments;
        this.postCategory = postCategory;
    }

    /**
     * 생성 메서드
     */
    public static Post createPost(String title, String content, User user, PostCategory postCategory){
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .postCategory(postCategory)
                .build();
    }

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 초기화 값이 DB 에 추가되지 않는 오류가 있어서
     * persist 하기 전에 초기화
     */
    @PrePersist
    public void prePersistCount(){
        this.viewCount = this.viewCount == null ? 0 : this.viewCount;
    }
}
