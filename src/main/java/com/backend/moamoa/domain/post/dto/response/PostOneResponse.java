package com.backend.moamoa.domain.post.dto.response;

import com.backend.moamoa.domain.post.entity.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PostOneResponse {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer viewCount;
    private String username;


    @QueryProjection
    public PostOneResponse(Long postId, String title, String content, LocalDateTime createDate, LocalDateTime updateDate, Integer viewCount, String username) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.viewCount = viewCount;
        this.username = username;
    }

}
