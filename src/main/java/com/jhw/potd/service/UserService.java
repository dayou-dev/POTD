package com.jhw.potd.service;

import static com.jhw.potd.global.SessionConstant.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhw.potd.controller.dto.response.FeedResponse;
import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;
import com.jhw.potd.global.encryption.EncryptPasswordEncoder;
import com.jhw.potd.repository.FeedRepository;
import com.jhw.potd.controller.dto.request.LoginRequest;
import com.jhw.potd.controller.dto.request.SignUpRequest;
import com.jhw.potd.controller.dto.response.UserProfileResponse;
import com.jhw.potd.repository.UserRepository;
import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final FeedRepository feedRepository;
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
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		User user = userRepository.findByEmailAndPassword(
				req.getEmail(),
				encoder.encode(req.getPassword()))
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		session.setAttribute(USER_ID, user.getId());
	}

	@Transactional
	public void logout(Long userId) {
		if (session.getAttribute(USER_ID) != null) {
			session.removeAttribute(USER_ID);
		}
	}

	public UserProfileResponse getMyProfile(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		List<Feed> feeds = feedRepository.findAllByUser(user);
		return new UserProfileResponse(user.getId(), user.getNickname(),
			feeds.stream().map(feed -> new FeedResponse()).toList());
	}

	public UserProfileResponse getProfile(Long targetId) {
		User user = userRepository.findById(targetId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<Feed> feeds = feedRepository.findAllByUser(user);
		return new UserProfileResponse(user.getId(), user.getNickname(),
			feeds.stream().map(feed -> new FeedResponse()).toList());
	}

	public String getLoginUser() {
		return (String) session.getAttribute(USER_ID);
	}
}
