package com.backend.moamoa.domain.post.repository;


import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.dto.response.QPostOneResponse;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.QPost;
import com.backend.moamoa.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.backend.moamoa.domain.post.entity.QPost.*;
import static com.backend.moamoa.domain.user.entity.QUser.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findMyPostsById(Long id) {
        return null;
    }

    @Override
    public Optional<PostOneResponse> findOnePostById(Long postId) {
        queryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        return Optional.ofNullable(queryFactory
                .select(new QPostOneResponse(
                        post.id,
                        post.title,
                        post.content,
                        post.timeEntity.createdDate,
                        post.timeEntity.updatedDate,
                        post.viewCount,
                        user.nickname))
                .from(post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId))
                .fetchOne());
    }
}
