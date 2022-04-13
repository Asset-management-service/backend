package com.backend.moamoa.domain.user.oauth.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    @ApiModelProperty(value = "accessToken의 타입", example = "Bearer")
    private String grantType;

    @ApiModelProperty(value = "발급된 accessToken")
    private String accessToken;

    @ApiModelProperty(value = "발급된 refreshToken")
    private String refreshToken;

    @ApiModelProperty(value = "accessToken의 만료 기간 ms(30분)", example = "60000")
    private Long accessTokenExpireDate;

}
