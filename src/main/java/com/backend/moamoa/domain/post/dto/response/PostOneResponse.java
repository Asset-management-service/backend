package com.backend.moamoa.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "결과 응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class PostOneResponse {

    @ApiModelProperty(value = "게시글 Id")
    private Long postId;

    @ApiModelProperty(value = "게시글 제목")
    private String title;

    @ApiModelProperty(value = "게시글 내용")
    private String content;

    @ApiModelProperty(value = "해당 게시글의 전체 스크랩 수")
    private int scrapCount;

    @ApiModelProperty(value = "해당 게시글의 전체 댓글의 수")
    private int commentCount;

    @ApiModelProperty(value = "해당 게시글의 전체 좋아요 수")
    private int likeCount;

    @ApiModelProperty(value = "해당 게시글의 생성 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "해당 게시글의 수정 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updateDate;

    @ApiModelProperty(value = "해당 게시글의 조회수")
    private Integer viewCount;

    @ApiModelProperty(value = "해당 게시글의 생성 회원 이름")
    private String username;

    @ApiModelProperty(value = "해당 게시글의 유저 본인 확인")
    private boolean myPost;

    @ApiModelProperty(value = "해당 게시글의 좋아요 본인 확인")
    private boolean myLike;

    @ApiModelProperty(value = "해당 게시글의 스크랩 본인 확인")
    private boolean myScrap;

    @ApiModelProperty(value = "해당 게시글에 저장된 이미지 경로")
    private List<String> imageUrl = new ArrayList<>();

    @ApiModelProperty(value = "해당 게시글의 댓글")
    private List<PostOneCommentResponse> comments = new ArrayList<>();

    @QueryProjection
    public PostOneResponse(Long postId, String title, String content, int scrapCount, int commentCount, int likeCount, LocalDateTime createDate, LocalDateTime updateDate, Integer viewCount, String username, boolean myPost, boolean myLike, boolean myScrap) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.scrapCount = scrapCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.viewCount = viewCount;
        this.username = username;
        this.myPost = myPost;
        this.myLike = myLike;
        this.myScrap = myScrap;
    }

}
