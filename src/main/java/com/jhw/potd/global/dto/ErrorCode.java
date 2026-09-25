package com.jhw.potd.global.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 400 BAD_REQUEST: 잘못된 요청
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

	// 404 NOT_FOUND: 리소스를 찾을 수 없음
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
	FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
	// 409 CONFLICT: 데이터 중복
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
	DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "사용중인 닉네임입니다.")

	;
	private final HttpStatus status;
	private final String message;
}
