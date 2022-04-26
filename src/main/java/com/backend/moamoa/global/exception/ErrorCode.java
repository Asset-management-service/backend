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

    NOT_FOUND_MONEY_LOG(HttpStatus.NOT_FOUND, "해당 머니 로그를 찾을 수 없습니다."),

    NOT_FOUND_BUDGET(HttpStatus.NOT_FOUND, "예산 금액을 찾을 수 없습니다."),

    NOT_FOUND_ASSET_CATEGORY(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),

    NOT_FOUND_ASSET(HttpStatus.NOT_FOUND, "해당 가계부 설정을 찾을 수 없습니다."),

    NOT_FOUND_REVENUE_EXPENDITURE(HttpStatus.NOT_FOUND, "해당 수익 지출 내역을 찾을 수 없습니다."),

    NOT_FOUND_ASSET_GOAL(HttpStatus.NOT_FOUND, "해당 자산 관리 목표를 찾을 수 없습니다."),

    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다"),

    INVALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 엑세스 토큰입니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 리프레쉬 토큰입니다."),

    BAD_REQUEST_EXPENDITURE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 100%를 맞춰주세요."),

    ALREADY_NICKNAME_EXISTS(HttpStatus.ALREADY_REPORTED, "이미 존재하는 닉네임입니다."),

    ALREADY_PHONE_NUM_EXISTS(HttpStatus.ALREADY_REPORTED, "이미 존재하는 휴대폰 번호 입니다."),

    NOT_FOUND_RATIO(HttpStatus.NOT_FOUND, "지출 비율을 찾을 수 없습니다.");




    private final HttpStatus httpStatus;
    private final String detail;

}
