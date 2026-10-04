package com.jhw.potd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.Like;
import com.jhw.potd.domain.User;
import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;
import com.jhw.potd.repository.FeedRepository;
import com.jhw.potd.repository.LikeRepository;
import com.jhw.potd.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
	private final LikeRepository likeRepository;
	private final UserRepository userRepository;
	private final FeedRepository feedRepository;

	@Transactional
	public boolean feedLike(Long userId, Long feedId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
			new CustomException(ErrorCode.FEED_NOT_FOUND));
		if (likeRepository.existsByUserAndFeed(user, feed)) {
			likeRepository.deleteByUserAndFeed(user, feed);
			return false;
		}
		Like like = Like.builder()
			.user(user)
			.feed(feed)
			.build();
		likeRepository.save(like);
		return true;
	}
}
