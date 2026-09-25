package com.jhw.potd;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
