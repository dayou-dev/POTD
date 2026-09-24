package com.jhw.potd;

import java.security.PublicKey;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

	private final FeedRepository feedRepository;
	private final UserRepository userRepository;

	@Transactional
	public void publisFeed(Long userId, FeedRequest request) {
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
}
