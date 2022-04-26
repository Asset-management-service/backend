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
public class UpdateMoneyLogResponse {

    @ApiModelProperty(value = "머니 로그 PK")
    private Long moneyLogId;

    @ApiModelProperty(value = "저장된 이미지 경로")
    private List<String> imageUrl = new ArrayList<>();

}
