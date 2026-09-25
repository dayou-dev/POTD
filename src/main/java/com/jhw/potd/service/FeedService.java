package com.jhw.potd.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhw.potd.controller.dto.response.FeedResponse;
import com.jhw.potd.global.dto.ApiResponse;
import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;
import com.jhw.potd.global.dto.GlobalExceptionHandler;
import com.jhw.potd.repository.FeedRepository;
import com.jhw.potd.controller.dto.request.FeedRequest;
import com.jhw.potd.repository.UserRepository;
import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

	private final FeedRepository feedRepository;
	private final UserRepository userRepository;

	@Transactional
	public void publishFeed(Long userId, FeedRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Feed feed = Feed.builder().user(user).imgUrl(request.getImgUrl()).content(request.getContent()).build();
		feedRepository.save(feed);
	}

	public FeedResponse getFeed(Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
			new CustomException(ErrorCode.FEED_NOT_FOUND));
		return new FeedResponse(feed);
	}

	public List<FeedResponse> getFeeds() {
		return feedRepository.findAll().stream().map(feed -> new FeedResponse()).toList();
	}

	@Transactional
	public void deleteFeed(Long userId, Long feedId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
			new CustomException(ErrorCode.FEED_NOT_FOUND));
		if (!feed.getUser().getId().equals(user.getId())) {
			throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
		}
		feedRepository.delete(feed);

	}
}
