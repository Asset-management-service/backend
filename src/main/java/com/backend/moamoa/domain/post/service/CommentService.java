package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.request.CommentUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.CommentResponse;
import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.repository.comment.CommentRepository;
import com.backend.moamoa.domain.post.repository.post.PostRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserUtil userUtil;

    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest) {
        User user = userUtil.findCurrentUser();
        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        Long parentId = commentRequest.getParentId();

        if (Objects.isNull(parentId)) {
            return createParentComment(commentRequest, user, post);
        }
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));
        Comment comment = commentRepository.save(Comment.createComment(parent, user, post, commentRequest.getContent()));
        return new CommentResponse(comment.getId(), comment.getUser().getNickname(), comment.getTimeEntity().getCreatedDate());
    }

    private CommentResponse createParentComment(CommentRequest commentRequest, User user, Post post) {
        Comment comment = commentRepository.save(Comment.builder()
                .user(user)
                .post(post)
                .content(commentRequest.getContent())
                .build());
        return new CommentResponse(comment.getId(), comment.getUser().getNickname(), comment.getTimeEntity().getCreatedDate());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User user = userUtil.findCurrentUser();

        commentRepository.delete(getComment(commentId, user));
    }

    @Transactional
    public Long updateComment(CommentUpdateRequest request) {
        User user = userUtil.findCurrentUser();
        Comment comment = getComment(request.getCommentId(), user);
        comment.updateContent(request.getContent());

        return comment.getId();
    }

    /**
     * 유저와 댓글의 연관관계를 확인하는 공통 로직 메소드
     */
    private Comment getComment(Long commentId, User user) {
        return commentRepository.findWithPostAndMemberById(user.getId(), commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_USER));
    }
}
