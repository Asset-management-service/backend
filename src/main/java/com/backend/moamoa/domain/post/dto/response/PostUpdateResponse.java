package com.backend.moamoa.domain.post.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "결과 응답 데이터 모델")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateResponse {

        @ApiModelProperty(value = "게시글 Id")
        private Long postId;

        @ApiModelProperty(value = "응답 메세지")
        private String message;

        private List<String> deleteImages = new ArrayList<>();

        private List<String> saveImages = new ArrayList<>();

}
