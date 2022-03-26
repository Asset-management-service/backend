package com.backend.moamoa.domain.post.entity.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PostDetailResponseDto {

    private Long userId;
    private Long postId;
    private String writer;
    private String title;
    private String context;
    private LocalDateTime createdDate;

    @QueryProjection
    public PostDetailResponseDto(Long userId, Long postId, String writer,String title, String context, LocalDateTime createdDate) {
        this.userId = userId;
        this.postId = postId;
        this.writer = writer;
        this.title = title;
        this.context = context;
        this.createdDate = createdDate;
    }
}
