package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.request.LogoutRequest;
import com.backend.moamoa.domain.user.dto.request.TokenRequest;
import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "권한 API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "엑세스, 리프레시 토큰 재발급",
            notes = "엑세스 토큰 만료시 회원 검증 후, 리프레시 토큰을 검증하여 엑세스 토큰과 리프레시 토큰을 재발급 합니다.")
    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody TokenRequest tokenRequest) {
        return authService.reissue(tokenRequest);
    }

    @ApiOperation(value = "로그아웃",
            notes = "엑세스, 리프레시 토큰 검증 후 로그아웃을 진행 합니다. 로그아웃 성공시 true를 반환합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@RequestBody LogoutRequest logoutRequest) {
        return ResponseEntity.ok(authService.logout(logoutRequest));
    }

}
