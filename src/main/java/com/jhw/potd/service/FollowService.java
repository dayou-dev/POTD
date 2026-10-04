package com.jhw.potd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhw.potd.domain.Follow;
import com.jhw.potd.domain.User;
import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;
import com.jhw.potd.repository.FollowRepository;
import com.jhw.potd.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final UserRepository userRepository;
	private final FollowRepository followRepository;

	@Transactional
	public void follow(Long userId, Long targetId) {
		if (userId.equals(targetId)) {
			throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
		}
		User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		User target = userRepository.findById(targetId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		if (followRepository.existsByFollowerAndFollowing(me, target)) {
			throw new IllegalArgumentException("이미 팔로우 상태");
		}
		Follow follow = Follow.builder().follower(me).following(target).build();
		followRepository.save(follow);
	}

	@Transactional
	public void unfollow(Long userId, Long targetId) {
		User me = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		User target = userRepository.findById(targetId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		if (!followRepository.existsByFollowerAndFollowing(me, target)) {
			throw new IllegalArgumentException("이미 팔로우가 해제된 상태입니다.");
		}
		followRepository.deleteByFollowerAndFollowing(me, target);
	}
}
