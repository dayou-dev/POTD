package com.jhw.potd;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/v1")
public class FeedApiController {

	private final FeedService feedService;

	@PostMapping
	public void publishFeeds(Long userId, @RequestBody FeedRequest request) {
		feedService.publisFeed(userId, request);
	}

	@GetMapping("/{feedId}")
	public Feed getFeed(@PathVariable Long feedId) {
		return feedService.getFeed(feedId);
	}

	@GetMapping
	public List<Feed> getFeeds() {
		return feedService.getFeeds();
	}
}
