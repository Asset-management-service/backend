package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.request.CommentUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.CommentResponse;
import com.backend.moamoa.domain.post.dto.response.UpdateCommentResponse;
import com.backend.moamoa.domain.post.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "댓글 작성", notes = "parentId는 댓글인 경우 null, 답글인 경우 부모 PK를 입력해주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "해당 댓글이 정상적으로 생성된 경우"),
            @ApiResponse(responseCode = "404", description = "회원 OR 게시글의 ID가 없는 경우")
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(request));
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글의 Id 값을 받아 해당 댓글을 삭제하는 API")
    @ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "해당 댓글이 정상적으로 삭제된 경우"),
            @ApiResponse(responseCode = "404", description = "회원의 Id 값을 찾지 못한 경우"),
            @ApiResponse(responseCode = "403", description = "해당 댓글을 삭제할 권한이 없는 경우")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value = "댓글 수정", notes = "댓글의 ID 와 내용을 입력받아 해당 댓글을 갱신하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 댓글이 정상적으로 갱신된 경우"),
            @ApiResponse(responseCode = "404", description = "회원의 Id 값을 찾지 못한 경우"),
            @ApiResponse(responseCode = "403", description = "해당 댓글을 수정할 권한이 없는 경우")
    })
    @PatchMapping
    public ResponseEntity<UpdateCommentResponse> updateComment(@RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(new UpdateCommentResponse(commentService.updateComment(request)));
    }
}
