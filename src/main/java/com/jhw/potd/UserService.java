package com.jhw.potd;

import static com.jhw.potd.SessionConstant.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final EncryptPasswordEncoder encoder;
	private final HttpSession session;

	@Transactional
	public void signUp(SignUpRequest req) {
		User user = User.builder()
			.email(req.getEmail())
			.password(encoder.encode(req.getPassword()))
			.nickname(req.getNickname())
			.build();
		userRepository.save(user);
	}

	@Transactional
	public void login(LoginRequest req) {
		if (!userRepository.existsByEmail(req.getEmail())) {
			throw new EntityNotFoundException("User not found");
		}
		User user = userRepository.findByEmailAndPassword(
				req.getEmail(),
				encoder.encode(req.getPassword()))
			.orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
		session.setAttribute(USER_ID, user.getId());
	}
}
