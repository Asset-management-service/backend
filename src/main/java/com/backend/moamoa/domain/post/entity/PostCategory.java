package com.backend.moamoa.domain.post.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PostCategory{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_category_id")
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "postCategory", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();


    @Builder
    public PostCategory(String categoryName, List<Post> posts) {
        this.categoryName = categoryName;
        this.posts = posts;
    }


    public static PostCategory createCategory(String categoryName) {
        return PostCategory.builder()
                .categoryName(categoryName)
                .build();
    }


}
