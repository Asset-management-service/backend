package com.backend.moamoa.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel(description = "결과 응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class RecentPostResponse {

    @ApiModelProperty(value = "게시글 Id")
    private Long postId;

    @ApiModelProperty(value = "카테고리 이름")
    private String categoryName;

    @ApiModelProperty(value = "게시글 제목")
    private String title;

    @ApiModelProperty(value = "게시글 내용")
    private String content;

    @ApiModelProperty(value = "해당 게시글의 댓글의 수")
    private int commentCount;

    @ApiModelProperty(value = "게시글을 작성한 회원 이름")
    private String username;

    @ApiModelProperty(value = "게시글 생성 시간")
    private LocalDateTime localDateTime;

    @ApiModelProperty(value = "해당 게시글의 조회수")
    private int viewCount;

    @QueryProjection
    public RecentPostResponse(Long postId, String categoryName, String title, String content, int commentCount, String username, LocalDateTime localDateTime, int viewCount) {
        this.postId = postId;
        this.categoryName = categoryName;
        this.title = title;
        this.content = content;
        this.commentCount = commentCount;
        this.username = username;
        this.localDateTime = localDateTime;
        this.viewCount = viewCount;
    }
}
