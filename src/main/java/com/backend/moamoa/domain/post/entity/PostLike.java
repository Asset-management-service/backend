package com.backend.moamoa.domain.post.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class PostLike {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_like_id")
    private Long id;


    @ManyToOne(fetch = LAZY, cascade = PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY, cascade = PERSIST)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public static PostLike createPostLike(User user, Post post) {
        return PostLike.builder()
                .user(user)
                .post(post)
                .build();
    }
}
