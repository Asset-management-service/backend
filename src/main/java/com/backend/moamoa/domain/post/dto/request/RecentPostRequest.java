package com.backend.moamoa.domain.post.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "게시글 조회 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class RecentPostRequest {

    @ApiModelProperty(value = "해당 게시글의 카테고리 이름", example = "모아모아", required = true)
    private String categoryName;

}
