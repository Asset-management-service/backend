package com.backend.moamoa.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {


    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "해당 글을 찾을 수 없습니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String detail;

}
