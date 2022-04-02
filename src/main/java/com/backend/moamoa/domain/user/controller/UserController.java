package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.UserResponse;
import com.backend.moamoa.domain.user.service.UserService;
import com.backend.moamoa.global.common.ApiResponse;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUtil userUtil;

    private final UserService userService;

    /** [GET] /users
     * 사용자의 accessToken을 받아 사용자의 정보를 반환합니다.
     * @return ApiResponse 응답 내용
     */
    @GetMapping
    public ApiResponse getUser() {
        User user = userUtil.findCurrentUser();
        return ApiResponse.success("user", user);
    }

    /**
     * [PATCH] /users
     * 현재 사용자의 개인 정보를 변경합니다.
     * @return 사용자 개인정보 수정
     */
    @PatchMapping
    public UserResponse update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        User user = userUtil.findCurrentUser();
        return userService.update(user, userUpdateRequest);
    }

}
