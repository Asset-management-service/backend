package com.backend.moamoa.domain.user.oauth.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("토큰 재발급 요청 정보")
public class TokenRequest {

    @NotNull
    @ApiModelProperty(value = "만료된 accessToken", required = true)
    private String accessToken;

    @NotNull
    @ApiModelProperty(value = "유저의 refreshToken", required = true)
    private String refreshToken;

}
