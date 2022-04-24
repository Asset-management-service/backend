package com.backend.moamoa.domain.asset.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@ApiModel(description = "자산 관리 목표 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssetGoalRequest {

    @ApiModelProperty(value = "해당 년, 월, 일", example = "2022-04-23", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @ApiModelProperty(value = "자산 관리 목표 내용", example = "100만원 적금 하기!", required = true)
    private String content;

}
