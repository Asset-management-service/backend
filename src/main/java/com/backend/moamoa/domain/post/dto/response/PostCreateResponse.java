package com.backend.moamoa.domain.post.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {

    @ApiModelProperty(value = "게시글 Id")
    private Long postId;

    @ApiModelProperty(value = "이미지 경로")
    private List<String> imageUrl = new ArrayList<>();
}

