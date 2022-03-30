package com.backend.moamoa.domain.post.repository;


import com.backend.moamoa.domain.post.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findMyPostsById(Long id) {
        return null;
    }
}
