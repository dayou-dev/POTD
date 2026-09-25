package com.jhw.potd.global.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<?>> handleBusinessException(CustomException e) {
		ErrorCode code = e.getErrorCode();
		return ResponseEntity.badRequest().body(ApiResponse.fail(code.getStatus().toString(), code.getMessage(), null));
	}
}
