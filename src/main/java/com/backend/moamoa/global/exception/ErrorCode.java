package com.backend.moamoa.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    BAD_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND_STOCK(HttpStatus.NOT_FOUND,"해당 주식이름이 존재하지않습니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND,"해당 게시물이 존재하지않습니다.");

    private final HttpStatus httpStatus;
    private final String detail;

}
