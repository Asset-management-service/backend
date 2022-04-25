package com.backend.moamoa.domain.asset.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "머니로그 조회 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyLogResponse {

    @ApiModelProperty(value = "머니로그 PK")
    private Long moneyLogId;

    @ApiModelProperty(value = "머니로그 내용")
    private String content;

    @ApiModelProperty(value = "머니로그 이미지 경로")
    private List<String> imageUrl = new ArrayList<>();

}
