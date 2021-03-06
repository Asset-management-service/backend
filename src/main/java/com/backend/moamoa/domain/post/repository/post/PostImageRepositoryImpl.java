package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.entity.PostImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.backend.moamoa.domain.asset.entity.QMoneyLog.moneyLog;
import static com.backend.moamoa.domain.post.entity.QPost.post;
import static com.backend.moamoa.domain.post.entity.QPostImage.postImage;

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

    @Override
    public List<PostImage> findBySavedMoneyLogImageUrl(Long moneyLogId) {
        return queryFactory
                .selectFrom(postImage)
                .innerJoin(postImage.moneyLog, moneyLog)
                .where(moneyLog.id.eq(moneyLogId))
                .fetch();
    }

}
