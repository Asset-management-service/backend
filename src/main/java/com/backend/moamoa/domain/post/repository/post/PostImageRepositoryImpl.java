package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.entity.PostImage;
import com.backend.moamoa.domain.post.entity.QPost;
import com.backend.moamoa.domain.post.entity.QPostImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.backend.moamoa.domain.post.entity.QPost.*;
import static com.backend.moamoa.domain.post.entity.QPostImage.*;

@RequiredArgsConstructor
public class PostImageRepositoryImpl implements PostImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostImage> findBySavedImageUrl(Long postId) {
        return queryFactory
                .selectFrom(postImage)
                .innerJoin(postImage.post, post)
                .where(post.id.eq(postId))
                .fetch();
    }
}
