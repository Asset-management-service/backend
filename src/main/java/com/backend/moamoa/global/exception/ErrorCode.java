package com.backend.moamoa.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "해당 이메일 인증 토큰을 찾을 수 없습니다."),

    ALREADY_EMAIL_EXISTS(HttpStatus.ALREADY_REPORTED, "이미 인증된 이메일 입니다."),

    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "해당 글을 찾을 수 없습니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "해당 뎃글을 찾을 수 없습니다."),

    NOT_FOUND_ASSET(HttpStatus.NOT_FOUND, "해당 가계부 설정을 찾을 수 없습니다."),

    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다");



    private final HttpStatus httpStatus;
    private final String detail;

}
