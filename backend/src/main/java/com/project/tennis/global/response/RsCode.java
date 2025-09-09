package com.project.tennis.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RsCode {

    // Common
    FORBIDDEN(RsConstant.FORBIDDEN, "접근 권한이 없습니다."),
    SUCCESS(RsConstant.SUCCESS, "요청이 성공했습니다."),
    CREATED(RsConstant.CREATED, "새로운 리소스를 생성했습니다."),
    NOT_FOUND(RsConstant.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    BAD_REQUEST(RsConstant.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER(RsConstant.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    UNAUTHENTICATED(RsConstant.UNAUTHORIZED, "인증이 실패했습니다."),
    CONFLICT(RsConstant.CONFLICT, "리소스의 충돌이 있습니다."),
    UNAUTHORIZED(RsConstant.FORBIDDEN, "접근 권한이 없습니다."),
    TOO_MANY_REQUESTS(RsConstant.TOO_MANY_REQUESTS, "너무 많은 요청입니다."),

    // Member
    DUPLICATE_USERNAME(RsConstant.BAD_REQUEST, "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(RsConstant.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(RsConstant.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    PASSWORD_NOT_CORRECT(RsConstant.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USERNAME_NOT_EXIST(RsConstant.BAD_REQUEST, "존재하지 않는 아이디입니다."),
    EMAIL_NOT_EXIST(RsConstant.BAD_REQUEST, "존재하지 않는 이메일입니다."),

    // TennisCourt
    DUPLICATE_TENNIS_COURT_NAME(RsConstant.BAD_REQUEST, "이미 사용 중인 테니스장 이름입니다.");


    private final Integer code;
    private final String message;

}
