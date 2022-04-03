package com.backend.moamoa.domain.post.repository;


import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.backend.moamoa.domain.post.entity.QComment.comment;
import static com.backend.moamoa.domain.post.entity.QPost.post;
import static com.backend.moamoa.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findMyPostsById(Long id) {
        return null;
    }

    @Override
    public PostOneResponse findOnePostById(Long postId) {
        queryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        PostOneResponse response = queryFactory
                .select(new QPostOneResponse(
                        post.id,
                        post.title,
                        post.content,
                        post.scraps.size(),
                        post.comments.size(),
                        post.postLikes.size(),
                        post.timeEntity.createdDate,
                        post.timeEntity.updatedDate,
                        post.viewCount,
                        user.nickname))
                .from(post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId))
                .fetchOne();

        List<PostOneCommentResponse> comments = queryFactory
                .select(new QPostOneCommentResponse(
                        comment.post.id,
                        comment.parent.id,
                        comment.id,
                        comment.content,
                        user.nickname,
                        comment.timeEntity.createdDate,
                        comment.timeEntity.updatedDate))
                .from(comment)
                .innerJoin(comment.post, post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId))
                .orderBy(comment.parent.id.desc())
                .fetch();


         response.setComments(comments);

         return response;
    }


}
