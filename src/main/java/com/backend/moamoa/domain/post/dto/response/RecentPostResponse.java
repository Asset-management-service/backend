package com.backend.moamoa.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RecentPostResponse {

    private Long postId;

    private String categoryName;

    private String title;

    private String content;

    private int commentCount;

    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime localDateTime;

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
