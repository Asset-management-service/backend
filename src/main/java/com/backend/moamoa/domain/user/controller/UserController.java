package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.request.UserEmailRequest;
import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.MyCommentResponse;
import com.backend.moamoa.domain.user.dto.response.MyPostResponse;
import com.backend.moamoa.domain.user.dto.response.UserResponse;
import com.backend.moamoa.domain.user.entity.UserMailAuth;
import com.backend.moamoa.domain.user.service.MailSendService;
import com.backend.moamoa.domain.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "유저 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailSendService mailSendService;

    /**
     * [GET] api/users
     * 사용자의 accessToken을 받아 사용자의 정보를 반환합니다.
     *
     * @return ApiResponse 응답 내용
     */
    @GetMapping
    @ApiOperation(value = "현재 사용자 조회",
            notes = "현재 사용자의 정보를 가져옵니다. 헤더(Bearer)에 사용자 토큰 주입을 필요로 합니다.")
    public UserResponse getUser() {
        return userService.getUser();
    }

    /**
     * [PATCH] api/users
     * 현재 사용자의 개인 정보를 변경합니다.
     *
     * @return 사용자 개인정보 수정
     */
    @PatchMapping
    @ApiOperation(value = "개인정보 수정",
            notes = "현재 사용자의 개인정보를 업데이트 합니다. 헤더(Bearer)에 사용자 토큰 주입을 필요로 합니다.")
    public UserResponse update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return userService.update(userUpdateRequest);
    }

    /**
     * [GET] api/users/registerEmail
     * 변경할 이메일을 받아 이메일 인증 메일을 보냅니다.
     */
    @GetMapping("/registerEmail")
    @ApiOperation(value = "이메일 인증하기",
            notes = "변경할 이메일을 받아 인증 메일을 보내고 이메일을 인증합니다. 헤더(Bearer)에 사용자 토큰 주입을 필요로 합니다.")
    public void registerEmail(@RequestBody UserEmailRequest userEmailRequest) {
        userService.isDuplicateEmail(userEmailRequest.getEmail());
        String authKey = mailSendService.sendAuthMail(userEmailRequest.getEmail());
        mailSendService.save(userEmailRequest.getEmail(), authKey);
    }

    @GetMapping("/confirm")
    @ApiOperation(value = "이메일 인증",
            notes = "사용자가 전송받은 인증 이메일의 인증하기 버튼을 누르면 인증 로직을 시행합니다.")
    @ApiIgnore
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

    /**
     * [GET] api/users/mypage/posts&page= &size= &sort=
     *
     * page : 가져올 페이지 (기본값 : 0)
     * size : 페이지의 크기 (기본값 : 20)
     * sort : 정렬 기준으로 사용할 속성으로 기본적으로 오름차순
     *
     * 마이 페이지의 내가 쓴 글의 리스트를 최신순으로 반환합니다.
     */
    @ApiOperation(value = "마이 페이지 내가 쓴 글 보기",
            notes = "마이 페이지의 내가 쓴 글의 리스트를 반환합니다. 헤더(Bearer)에 사용자 토큰 주입을 필요로 합니다.")
    @GetMapping("/mypage/posts")
    public Page<MyPostResponse> findMyPosts(Pageable pageable) {
        return userService.findMyPosts(pageable);
    }

    /**
     * [GET] api/users/mypage/comments
     *
     * page : 가져올 페이지 (기본값 : 0)
     * size : 페이지의 크기 (기본값 : 20)
     * sort : 정렬 기준으로 사용할 속성으로 기본적으로 오름차순
     *
     * 마이 페이지의 내가 쓴 댓글의 리스트를 반환합니다.
     */
    @ApiOperation(value = "마이 페이지 내가 쓴 댓글 보기",
            notes = "마이 페이지의 내가 쓴 글의 댓글을 반환합니다. 헤더(Bearer)에 사용자 토큰 주입을 필요로 합니다.")
    @GetMapping("/mypage/comments")
    public Page<MyCommentResponse> findMyComments(Pageable pageable) {
        return userService.findMyComments(pageable);
    }

}
