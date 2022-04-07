package com.backend.moamoa.domain.post.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "게시글 생성 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class PostRequest {

    @ApiModelProperty(value = "게시글 제목", example = "모아모아 화이팅!", required = true)
    private String title;

    @ApiModelProperty(value = "게시글 내용", example = "무야호", required = true)
    private String content;

    @ApiModelProperty(value = "커뮤니티 게시글의 카테고리 이름", example = "모아모아", required = true)
    private String categoryName;

}

