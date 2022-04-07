package com.backend.moamoa.domain.post.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "결과 응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateResponse {

    @ApiModelProperty(value = "갱신된 댓글의 ID")
    private Long commentId;

    @ApiModelProperty(value = "응답 메세지")
    private String message;

}
