package com.jhw.potd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhw.potd.controller.dto.request.LoginRequest;
import com.jhw.potd.controller.dto.request.SignUpRequest;
import com.jhw.potd.controller.dto.response.UserProfileResponse;
import com.jhw.potd.global.dto.ApiResponse;
import com.jhw.potd.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/v1")
public class UserApiController {

	private final UserService userService;

	@PostMapping("/sign-up")
	public void signUp(@RequestBody SignUpRequest req) {
		userService.signUp(req);
	}

	@PostMapping("/login")
	public void login(@RequestBody LoginRequest req) {
		userService.login(req);
	}

	@PostMapping("/logout")
	public void logout(Long userId) {
		userService.logout(userId);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Long userId) {
		return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(userId)));
	}

	@GetMapping("/{targetId}")
	public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long targetId) {
		return ResponseEntity.ok(ApiResponse.success(userService.getProfile(targetId)));
	}
}
