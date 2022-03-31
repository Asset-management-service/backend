package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.global.common.ApiResponse;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.service.UserService;
import com.backend.moamoa.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** [GET] /users
     * accessToken을 통해 유저 정보 가져오는 API
     * @return ApiResponse 응답 내용
     */
    @GetMapping
    public ApiResponse getUser() {
        User user = userService.getUser(SecurityUtil.getCurrentMemberId());
        return ApiResponse.success("user", user);
    }

}
