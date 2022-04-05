package com.backend.moamoa.domain.post.repository.post;


import com.backend.moamoa.domain.post.dto.request.RecentPostRequest;
import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.repository.post.PostCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.backend.moamoa.domain.post.entity.QComment.comment;
import static com.backend.moamoa.domain.post.entity.QPost.post;
import static com.backend.moamoa.domain.post.entity.QPostCategory.postCategory;
import static com.backend.moamoa.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;


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
                        comment.parent.id,
                        comment.id,
                        comment.content,
                        user.nickname,
                        comment.timeEntity.createdDate,
                        comment.timeEntity.updatedDate))
                .from(comment)
                .innerJoin(comment.post, post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId).and(comment.parent.id.isNull()))
                .orderBy(comment.id.asc())
                .fetch();

        List<CommentsChildrenResponse> children = queryFactory.select(new QCommentsChildrenResponse(
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
                .where(post.id.eq(postId).and(comment.parent.id.isNotNull()))
                .orderBy(comment.parent.id.asc())
                .fetch();

        List<Long> ids = comments
                .stream()
                .map(comment -> comment.getCommentId())
                .collect(Collectors.toList());

        List<CommentsChildrenResponse> collect = children.stream()
                .filter(child -> !child.getParentId().equals(ids))
                .collect(Collectors.toList());

        comments.stream()
                .forEach(comment -> comment.setChildren(children));

        response.setComments(comments);

         return response;
    }

    @Override
    public Page<RecentPostResponse> findRecentPosts(Pageable pageable, RecentPostRequest request) {
        List<RecentPostResponse> content = queryFactory
                .select(new QRecentPostResponse(
                        post.id,
                        postCategory.categoryName,
                        post.title,
                        post.content,
                        post.comments.size(),
                        user.nickname,
                        post.timeEntity.createdDate,
                        post.viewCount))
                .from(post)
                .where(postCategory.categoryName.eq(request.getCategoryName()))
                .innerJoin(post.postCategory, postCategory)
                .innerJoin(post.user, user)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(post.id.desc())
                .fetch();

        long countQuery = queryFactory
                .select(post.count())
                .from(post)
                .fetchCount();

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery);

    }
}
