package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "응답 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMoneyLogResponse {

    @ApiModelProperty(value = "머니 로그 생성 ID")
    private Long moneyLogId;

    @ApiModelProperty(value = "이미지 파일 생성 경로")
    private List<String> imageUrl = new ArrayList<>();

}
