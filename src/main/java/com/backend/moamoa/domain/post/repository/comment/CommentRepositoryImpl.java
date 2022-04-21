package com.backend.moamoa.domain.post.repository.comment;

import com.backend.moamoa.domain.post.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.backend.moamoa.domain.post.entity.QComment.comment;
import static com.backend.moamoa.domain.post.entity.QPost.post;
import static com.backend.moamoa.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Comment> findWithPostAndMemberById(Long userId, Long commentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(comment)
                .where(user.id.eq(userId).and(comment.id.eq(commentId)))
                .innerJoin(comment.user, user)
                .fetchOne());
    }

}
