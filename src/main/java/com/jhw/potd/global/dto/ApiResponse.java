package com.jhw.potd.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private String code;
	private String message;
	private T data;

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "200", "요청 성공", data);
	}

	public static <T> ApiResponse<T> fail(String code, String message, T data) {
		return new ApiResponse<>(false, code, message, data);
	}
}
