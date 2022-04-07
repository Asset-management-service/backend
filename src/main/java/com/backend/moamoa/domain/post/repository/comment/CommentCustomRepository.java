package com.backend.moamoa.domain.post.repository.comment;

import com.backend.moamoa.domain.post.dto.response.CommentsChildrenResponse;
import com.backend.moamoa.domain.post.dto.response.PostOneCommentResponse;
import com.backend.moamoa.domain.post.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentCustomRepository {

    Optional<Comment> findWithPostAndMemberById(Long userId, Long commentId);

    List<CommentsChildrenResponse> findPostComments(Long postId, Long commentId);

}
