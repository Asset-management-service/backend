package com.backend.moamoa.domain.post.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "게시물 댓글 작성 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @ApiModelProperty(value = "게시물 PK", example = "1", required = true)
    private Long postId;

    @ApiModelProperty(value = "부모 댓글 PK", example = "1", required = false)
    private Long parentId;

    @ApiModelProperty(value = "댓글 내용", example = "무야호~", required = true)
    private String content;

}
