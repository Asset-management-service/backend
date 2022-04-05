package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.request.CommentUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.CommentDeleteResponse;
import com.backend.moamoa.domain.post.dto.response.CommentResponse;
import com.backend.moamoa.domain.post.dto.response.CommentUpdateResponse;
import com.backend.moamoa.domain.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(@RequestBody CommentRequest request) {
        return commentService.createComment(request);
    }

    @DeleteMapping("/{commentId}")
    public CommentDeleteResponse deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @PatchMapping
    public CommentUpdateResponse updateComment(@RequestBody CommentUpdateRequest request) {
        return commentService.updateComment(request);
    }

}
