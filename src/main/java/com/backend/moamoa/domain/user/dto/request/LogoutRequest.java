package com.backend.moamoa.domain.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("로그아웃 요청 정보")
public class LogoutRequest {

    @NotNull
    @ApiModelProperty(value = "유저의 accessToken", example = "", required = true)
    private String accessToken;

    @NotNull
    @ApiModelProperty(value = "유저의 refreshToken", example = "", required = true)
    private String refreshToken;

}
