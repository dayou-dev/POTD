package com.jhw.potd.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhw.potd.controller.dto.request.FeedRequest;
import com.jhw.potd.controller.dto.response.FeedResponse;
import com.jhw.potd.domain.Feed;
import com.jhw.potd.global.dto.ApiResponse;
import com.jhw.potd.service.FeedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/v1")
public class FeedApiController {

	private final FeedService feedService;

	@PostMapping
	public void publishFeeds(Long userId, @RequestBody FeedRequest request) {
		feedService.publishFeed(userId, request);
	}

	@GetMapping("/{feedId}")
	public ResponseEntity<ApiResponse<FeedResponse>> getFeed(@PathVariable Long feedId) {
		return ResponseEntity.ok(ApiResponse.success(feedService.getFeed(feedId)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<FeedResponse>>> getFeeds() {
		return ResponseEntity.ok(ApiResponse.success(feedService.getFeeds()));
	}

	@DeleteMapping("/{feedId}")
	public void deleteFeed(Long userId, @PathVariable Long feedId) {
		feedService.deleteFeed(userId, feedId);

	}
}
