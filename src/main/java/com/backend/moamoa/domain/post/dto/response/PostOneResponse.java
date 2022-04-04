package com.backend.moamoa.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostOneResponse {

    private Long postId;

    private String title;

    private String content;

    private int scrapCount;

    private int commentCount;

    private int likeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updateDate;

    private Integer viewCount;

    private String username;

    private List<PostOneCommentResponse> comments = new ArrayList<>();

    @QueryProjection
    public PostOneResponse(Long postId, String title, String content, int scrapCount, int commentCount, int likeCount, LocalDateTime createDate, LocalDateTime updateDate, Integer viewCount, String username) {
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
    }

}
