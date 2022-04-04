package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.response.CommentResponse;
import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.repository.CommentRepository;
import com.backend.moamoa.domain.post.repository.PostRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest) {
        User user = userRepository.findById(1L).get();
        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        Long parentId = commentRequest.getParentId();

        if (Objects.isNull(parentId)) {
            return createParentComment(commentRequest, user, post);
        }
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));
        Comment comment = commentRepository.save(Comment.createComment(parent, user, post, commentRequest.getContent()));
        return new CommentResponse(comment.getId());
    }

    private CommentResponse createParentComment(CommentRequest commentRequest, User user, Post post) {
        Comment comment = commentRepository.save(Comment.builder()
                .user(user)
                .post(post)
                .content(commentRequest.getContent())
                .build());
        return new CommentResponse(comment.getId());
    }
}
