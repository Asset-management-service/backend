package com.backend.moamoa.domain.post.repository.comment;

import com.backend.moamoa.domain.post.entity.Comment;

import java.util.Optional;

public interface CommentCustomRepository {

    Optional<Comment> findWithPostAndMemberById(Long userId, Long commentId);
}
