package com.backend.moamoa.domain.user.dto.response;

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
@ApiModel("마이페이지 내가 쓴 글 정보")
public class MyPostResponse {

    @ApiModelProperty(value = "게시글 식별자", example = "1")
    private Long postId;

    @ApiModelProperty(value = "카테고리 이름", example = "자산관리 Q&A")
    private String categoryName;

    @ApiModelProperty(value = "게시글 이름", example = "제목제목제목")
    private String title;

    @ApiModelProperty(value = "댓글 수", example = "6")
    private int commentNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(value = "게시글 생성 시각")
    private LocalDateTime localDateTime;

    @ApiModelProperty(value = "조회 수", example = "1132")
    private int viewCount;

    public static MyPostResponse toMyPostResponse(Post post) {
        return MyPostResponse.builder()
                .postId(post.getId())
                .categoryName(post.getPostCategory().getCategoryName())
                .title(post.getTitle())
                .commentNum(post.getComments().size())
                .viewCount(post.getViewCount())
                .localDateTime(post.getTimeEntity().getCreatedDate())
                .build();

    }
}
