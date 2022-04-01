package com.backend.moamoa.domain.post.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Scrap {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Scrap(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public static Scrap createScrap(User user, Post post) {
        return Scrap.builder()
                .user(user)
                .post(post)
                .build();
    }
}
