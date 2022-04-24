package com.backend.moamoa.domain.asset.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "머니로그 수정 요청 데이터 모델")
@Getter
@Setter
@NoArgsConstructor
public class UpdateMoneyLogRequest {

    @ApiModelProperty(value = "해당 머니로그 PK", example = "1", required = true)
    @NotNull
    private Long moneyLogId;

    @ApiModelProperty(value = "해당 년, 월, 일", example = "2022-04-23", required = true)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ApiModelProperty(value = "머니 로그 작성 내용", example = "오늘은 떡볶이를 먹었다!", required = true)
    @NotNull
    private String content;

    @ApiModelProperty(value = "삭제한 이미지 경로를 제외한 남아있는 머니로그 이미지 경로")
    private List<String> saveImageUrl = new ArrayList<>();

    @ApiModelProperty(value = "이미지 파일")
    private List<MultipartFile> imageFiles = new ArrayList<>();

}
