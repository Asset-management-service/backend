package com.backend.moamoa.domain.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("이메일 인증 요청 정보")
public class UserEmailRequest {

    @NotNull
    @ApiModelProperty(value = "인증할 이메일", required = true, example = "kmw106933@naver.com")
    private String email;

}
