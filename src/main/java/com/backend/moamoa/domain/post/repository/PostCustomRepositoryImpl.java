package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.entity.QPost;
import com.backend.moamoa.domain.post.entity.dto.PostDetailResponseDto;
import com.backend.moamoa.domain.post.entity.dto.QPostDetailResponseDto;
import com.backend.moamoa.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.backend.moamoa.domain.post.entity.QPost.post;
import static com.backend.moamoa.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PostDetailResponseDto> findPostDetailDto(Long postId) {

        return Optional.ofNullable(queryFactory.select(new QPostDetailResponseDto(
                user.id,
                post.id,
                user.userProfile.nickname,
                post.title,
                post.context,
                post.timeEntity.createdDate
        )).from(post)
                .leftJoin(post.user,user)
                .where(post.id.eq(postId))
                .fetchOne());

    }
}
