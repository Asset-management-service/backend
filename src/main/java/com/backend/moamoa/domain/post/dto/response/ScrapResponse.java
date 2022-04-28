package com.backend.moamoa.domain.post.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel(description = "응답 데이터 모델")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponse {

    @ApiModelProperty(value = "스크랩 상태")
    private boolean scrapStatus;

}
