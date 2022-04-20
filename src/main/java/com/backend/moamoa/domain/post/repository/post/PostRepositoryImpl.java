package com.backend.moamoa.domain.post.repository.post;


import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.entity.Post;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.backend.moamoa.domain.post.entity.QComment.comment;
import static com.backend.moamoa.domain.post.entity.QPost.post;
import static com.backend.moamoa.domain.post.entity.QPostCategory.postCategory;
import static com.backend.moamoa.domain.post.entity.QPostImage.postImage;
import static com.backend.moamoa.domain.post.entity.QPostLike.postLike;
import static com.backend.moamoa.domain.post.entity.QScrap.scrap;
import static com.backend.moamoa.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PostOneResponse> findOnePostById(Long postId, Long userId) {
        queryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        Optional<PostOneResponse> response = Optional.ofNullable(queryFactory
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
                        user.nickname,
                        JPAExpressions
                                .selectFrom(post)
                                .where(user.id.eq(userId))
                                .exists(),
                        JPAExpressions
                                .selectFrom(postLike)
                                .where(postLike.post.eq(post).and(user.id.eq(userId)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(scrap)
                                .where(scrap.post.eq(post).and(user.id.eq(userId)))
                                .exists()))
                .from(post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId))
                .fetchOne());

        if (response.isEmpty()) {
            return Optional.empty();
        }

        List<String> postImages = queryFactory
                .select(postImage.imageUrl)
                .from(postImage)
                .innerJoin(postImage.post, post)
                .where(post.id.eq(postId))
                .fetch();

        response.get().setImageUrl(postImages);

        List<PostOneCommentResponse> comments = queryFactory
                .select(new QPostOneCommentResponse(
                        comment.parent.id,
                        comment.id,
                        comment.content,
                        user.nickname,
                        JPAExpressions
                                .selectFrom(comment)
                                .where(user.id.eq(userId))
                                .exists(),
                        comment.timeEntity.createdDate,
                        comment.timeEntity.updatedDate))
                .from(comment)
                .innerJoin(comment.post, post)
                .innerJoin(comment.user, user)
                .where(post.id.eq(postId).and(comment.parent.id.isNull()))
                .orderBy(comment.id.asc())
                .fetch();

        List<CommentsChildrenResponse> childComments = queryFactory
                .select(new QCommentsChildrenResponse(
                        comment.parent.id,
                        comment.id,
                        comment.content,
                        user.nickname,
                        JPAExpressions
                                .selectFrom(comment)
                                .where(user.id.eq(userId))
                                .exists(),
                        comment.timeEntity.createdDate,
                        comment.timeEntity.updatedDate
                ))
                .from(comment)
                .innerJoin(comment.post, post)
                .innerJoin(comment.user, user)
                .where(post.id.eq(postId).and(comment.parent.id.isNotNull()))
                .fetch();

        comments.stream()
                .forEach(parent -> {
                    parent.setChildren(childComments.stream()
                            .filter(child -> child.getParentId().equals(parent.getCommentId()))
                            .collect(Collectors.toList()));
                });

        response.get().setComments(comments);

         return response;
    }



    @Override
    public Page<RecentPostResponse> findRecentPosts(Pageable pageable, String categoryName) {
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
                .where(postCategory.categoryName.eq(categoryName))
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

    @Override
    public Optional<Post> findByIdAndUser(Long postId, Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(post)
                .innerJoin(post.user, user)
                .where(post.id.eq(postId).and(user.id.eq(userId)))
                .fetchOne());
    }
}
