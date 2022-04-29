package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.response.ReissueResponse;
import com.backend.moamoa.domain.user.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "권한 API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "엑세스, 리프레시 토큰 재발급",
            notes = "엑세스 토큰 만료시 회원 검증 후, 엑세스 토큰과 리프레시 토큰을 재발급 합니다. " +
                    "헤더(Bearer)에 엑세스 토큰 주입을 필요로 하며, 리프레시 토큰을 쿠키(쿠키명: refresh_token)로 주입해야 합니다.")
    @GetMapping("/reissue")
    @ApiResponse(responseCode = "200", description = "정상적으로 엑세스, 리프레시 토큰이 재발급된 경우")
    public ResponseEntity<ReissueResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(ReissueResponse.of(authService.reissue(request, response)));
    }

    @ApiOperation(value = "로그아웃",
            notes = "엑세스, 리프레시 토큰 검증 후 로그아웃을 진행 합니다. 로그아웃 성공시 true를 반환합니다. " +
                    "헤더(Bearer)에 엑세스 토큰 주입을 필요로 하며, 리프레시 토큰을 쿠키(쿠키명: refresh_token)로 주입해야 합니다.")
    @PostMapping("/logout")
    @ApiResponse(responseCode = "200", description = "정상적으로 로그아웃 된 경우")
    public ResponseEntity<Boolean> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }

}
