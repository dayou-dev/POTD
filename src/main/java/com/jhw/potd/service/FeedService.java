package com.jhw.potd.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾지 못했습니다."));
		Feed feed = Feed.builder().user(user).content(request.getContent()).build();
		feedRepository.save(feed);
	}

	public Feed getFeed(Long feedId) {
		return feedRepository.findById(feedId).orElseThrow(() ->
			new EntityNotFoundException("피드를 찾지 못했습니다."));
	}

	public List<Feed> getFeeds() {
		return feedRepository.findAll();
	}

	@Transactional
	public void deleteFeed(Long userId, Long feedId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾지 못했습니다."));
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글 입니다"));
		if (!feed.getUser().getId().equals(user.getId())) {
			throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
		}
		feedRepository.delete(feed);

	}
}
