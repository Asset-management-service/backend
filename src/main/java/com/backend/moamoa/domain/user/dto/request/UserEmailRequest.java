package com.backend.moamoa.domain.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("이메일 인증 요청 정보")
public class UserEmailRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @ApiModelProperty(value = "인증할 이메일", required = true, example = "kmw106933@naver.com")
    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$")
    private String email;

}
