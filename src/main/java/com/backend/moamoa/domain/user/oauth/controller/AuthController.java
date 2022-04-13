package com.backend.moamoa.domain.user.oauth.controller;

import com.backend.moamoa.domain.user.oauth.dto.request.TokenRequest;
import com.backend.moamoa.domain.user.oauth.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "토큰 재발급 API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "엑세스, 리프레시 토큰 재발급",
            notes = "엑세스 토큰 만료시 회원 검증 후, 리프레시 토큰을 검증하여 엑세스 토큰과 리프레시 토큰을 재발급 합니다.")
    @PostMapping("/reissue")
    public TokenResponse reissue(
            @ApiParam(value = "토큰 재발급 요청 데이터", required = true)
            @RequestBody TokenRequest tokenRequest) {
        return authService.reissue(tokenRequest);
    }

}
