package com.jhw.potd;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public void signUp(SignUpRequest req) {
		User user = User.builder()
			.email(req.getEmail())
			.password(req.getPassword())
			.nickname(req.getNickname())
			.build();
		userRepository.save(user);
	}

	@Transactional
	public User login(LoginRequest req) {
		if (!userRepository.existsByEmail(req.getEmail())) {
			throw new EntityNotFoundException("User not found");
		}
		User user = userRepository.findByEmailAndPassword(req.getEmail(), req.getPassword()).orElseThrow(
			() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
		return user;
	}

}
