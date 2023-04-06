package com.toomany.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ApiErrorCode {

    ENUM_CODE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_SUPPORT_OAUTH_PROVIDER(HttpStatus.NOT_FOUND, "지원하지 않는 소셜 플랫폼입니다."),
    SOCIAL_LOGIN(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인에 실패했습니다."),
    O_AUTH_TOKEN_BEFORE_EXPIRED(HttpStatus.BAD_REQUEST, "만료되지 않은 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    USER_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "잘못된 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
    KAKAO_SEARCH_PLACE(HttpStatus.INTERNAL_SERVER_ERROR, "매장을 찾는데 오류가 발생했습니다.\n관리자에게 문의하세요"),
    KAKAO_NOT_EXIST_PLACE(HttpStatus.NOT_FOUND, "카카오에 존재하지 않는 매장입니다."),
    KAKAO_SEARCH_REGION_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "지역 정보를 찾는에 오류가 발생했습니다.\n관리자에게 문의하세요."),
    KAKAO_NOT_EXIST_REGION_CODE(HttpStatus.NOT_FOUND, "카카오에 존재하지 않는 지역 정보입니다."),
    KAKAO_SEARCH_ADDRESS(HttpStatus.INTERNAL_SERVER_ERROR, "주소 정보를 찾는에 오류가 발생했습니다.\n관리자에게 문의하세요."),
    KAKAO_NOE_EXIST_ADDRESS(HttpStatus.NOT_FOUND, "카카오에 존재하지 않는 주소 정보입니다."),
    REGION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 주소가 없습니다.\n관리자에게 문의하세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
