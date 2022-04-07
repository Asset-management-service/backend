package com.backend.moamoa.domain.post.dto.response;

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
public class PostOneCommentResponse {

    private Long parentId;

    private Long commentId;

    private String content;

    private String username;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private List<CommentsChildrenResponse> children = new ArrayList<>();

    @QueryProjection
    public PostOneCommentResponse(Long parentId, Long commentId, String content, String username, LocalDateTime createDate, LocalDateTime updateDate) {
        this.parentId = parentId;
        this.commentId = commentId;
        this.content = content;
        this.username = username;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

}
