package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.request.UserEmailRequest;
import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.UserResponse;
import com.backend.moamoa.domain.user.entity.UserMailAuth;
import com.backend.moamoa.domain.user.service.MailSendService;
import com.backend.moamoa.domain.user.service.UserService;
import com.backend.moamoa.global.common.ApiResponse;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.utils.UserUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailSendService mailSendService;

    /**
     * [GET] api/users
     * 사용자의 accessToken을 받아 사용자의 정보를 반환합니다.
     * @return ApiResponse 응답 내용
     */
    @GetMapping
    @ApiOperation(value = "현재 사용자 조회",
            notes = "현재 사용자의 정보를 가져옵니다. 헤더에 사용자 토큰 주입을 필요로 합니다.")
    public ApiResponse getUser() {
        return userService.getUser();
    }

    /**
     * [PATCH] api/users
     * 현재 사용자의 개인 정보를 변경합니다.
     * @return 사용자 개인정보 수정
     */
    @PatchMapping
    @ApiOperation(value = "개인정보 수정",
            notes = "현재 사용자의 개인정보를 업데이트 합니다. 헤더에 사용자 토큰 주입을 필요로 합니다.")
    public UserResponse update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return userService.update(userUpdateRequest);
    }

    /**
     * [GET] api/users/registerEmail
     * 변경할 이메일을 받아 이메일 인증 메일을 보냅니다.
     */
    @GetMapping("/registerEmail")
    @ApiOperation(value = "이메일 전송",
            notes = "변경할 이메일을 받아 인증 메일을 보냅니다. 헤더에 사용자 토큰 주입을 필요로 합니다.")
    public void registerEmail(@RequestBody UserEmailRequest userEmailRequest) {
        String authKey = mailSendService.sendAuthMail(userEmailRequest.getEmail());
        mailSendService.save(userEmailRequest.getEmail(), authKey);
    }

    @GetMapping("/confirm")
    @ApiOperation(value = "이메일 인증",
            notes = "사용자가 전송받은 이메일의 인증하기 버튼을 누르면 인증 로직을 시행합니다.")
    public void confirmEmail(@RequestParam Map<String, String> map) {
        UserMailAuth authToken = mailSendService.getAuthToken(map);
        userService.confirmEmail(authToken);
    }

    /**
     * [GET] api/users/emailCheck/{userEmail}
     * 인증할 이메일을 받아, 이메일의 중복 여부를 확인 합니다.
     */
    @ApiOperation(value = "이메일 인증 중복 확인",
            notes = "DB에 입력된 이메일의 중복 여부를 리턴합니다. 이미 인증된 이메일이라면 true, 존재하지 않으면 false를 반환합니다.")
    @GetMapping("/emailCheck")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.isDuplicateEmail(email));
    }

}
