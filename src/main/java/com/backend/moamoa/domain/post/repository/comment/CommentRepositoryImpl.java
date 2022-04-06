package com.backend.moamoa.domain.post.repository.comment;

import com.backend.moamoa.domain.post.dto.response.CommentsChildrenResponse;
import com.backend.moamoa.domain.post.dto.response.PostOneCommentResponse;
import com.backend.moamoa.domain.post.dto.response.QCommentsChildrenResponse;
import com.backend.moamoa.domain.post.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
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
                .innerJoin(comment.post, post)
                .innerJoin(post.user, user)
                .fetchOne());
    }

    @Override
    public List<CommentsChildrenResponse> findPostComments(Long postId, Long commentId) {

        return queryFactory.select(new QCommentsChildrenResponse(
                        comment.parent.id,
                        comment.id,
                        comment.content,
                        user.nickname,
                        comment.timeEntity.createdDate,
                        comment.timeEntity.updatedDate))
                .from(comment)
                .innerJoin(comment.parent)
                .innerJoin(comment.post, post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId).and(comment.parent.id.eq(commentId)))
                .orderBy(comment.id.asc())
                .fetch();
    }
}
