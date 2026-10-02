package com.jhw.potd.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jhw.potd.controller.dto.response.FeedResponse;
import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;
import com.jhw.potd.repository.FeedRepository;
import com.jhw.potd.controller.dto.request.FeedRequest;
import com.jhw.potd.repository.UserRepository;
import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {
	private final S3ClientService s3ClientService;
	private final FeedRepository feedRepository;
	private final UserRepository userRepository;

	@Transactional
	public void publishFeed(Long userId, FeedRequest request, MultipartFile file) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Feed feed = Feed.builder().user(user).imgUrl(file.getOriginalFilename()).content(request.getContent()).build();
		s3ClientService.uploadImage(file);
		feedRepository.save(feed);
	}

	public FeedResponse getFeed(Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
			new CustomException(ErrorCode.FEED_NOT_FOUND));
		return new FeedResponse(feed);
	}

	public Page<FeedResponse> getFeeds(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Feed> feeds =  feedRepository.findAll(pageable);
		return feeds.map(FeedResponse::new);

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
