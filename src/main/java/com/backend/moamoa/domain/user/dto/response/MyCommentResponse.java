package com.backend.moamoa.domain.user.dto.response;

import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("마이페이지 내가 쓴 댓글 정보")
public class MyCommentResponse {

    @ApiModelProperty(value = "댓글 식별자", example = "1")
    private Long commentId;

    @ApiModelProperty(value = "게시글 식별자", example = "1")
    private Long postId;

    @ApiModelProperty(value = "댓글 내용", example = "1")
    private String content;

    @ApiModelProperty(value = "카테고리 이름", example = "자산관리 Q&A")
    private String categoryName;

    @ApiModelProperty(value = "게시글 이름", example = "제목제목제목")
    private String title;

    @ApiModelProperty(value = "댓글 수", example = "6")
    private int commentNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(value = "댓글 생성 시각")
    private LocalDateTime localDateTime;

    public static MyCommentResponse toMyCommentResponse(Comment comment) {
        return MyCommentResponse.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .categoryName(comment.getPost().getPostCategory().getCategoryName())
                .title(comment.getPost().getTitle())
                .commentNum(comment.getPost().getComments().size())
                .localDateTime(comment.getTimeEntity().getCreatedDate())
                .build();

    }

}
