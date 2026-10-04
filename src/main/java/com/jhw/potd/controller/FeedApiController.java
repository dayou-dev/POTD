package com.jhw.potd.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jhw.potd.controller.dto.request.ContentRequest;
import com.jhw.potd.controller.dto.response.CommentResponse;
import com.jhw.potd.controller.dto.response.FeedResponse;
import com.jhw.potd.global.LoginUser;
import com.jhw.potd.global.dto.ApiResponse;
import com.jhw.potd.service.CommentService;
import com.jhw.potd.service.FeedService;
import com.jhw.potd.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/v1")
public class FeedApiController {

	private final FeedService feedService;
	private final LikeService likeService;
	private final CommentService commentService;

	@PostMapping
	public void publishFeeds(@LoginUser Long userId, @RequestPart ContentRequest request,
		@RequestPart MultipartFile file) {
		feedService.publishFeed(userId, request, file);
	}

	@GetMapping("/{feedId}")
	public ResponseEntity<ApiResponse<FeedResponse>> getFeed(@PathVariable Long feedId) {
		return ResponseEntity.ok(ApiResponse.success(feedService.getFeed(feedId)));
	}

	@GetMapping("/list")
	public ResponseEntity<ApiResponse<Page<FeedResponse>>> getFeeds(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(ApiResponse.success(feedService.getFeeds(page, size)));
	}

	@DeleteMapping("/{feedId}")
	public void deleteFeed(Long userId, @PathVariable Long feedId) {
		feedService.deleteFeed(userId, feedId);
	}

	@PostMapping("/{feedId}/like")
	public ResponseEntity<ApiResponse<Boolean>> likeFeed(@LoginUser Long userId, @PathVariable Long feedId) {
		return ResponseEntity.ok(ApiResponse.success(likeService.feedLike(userId, feedId)));
	}

	@GetMapping("/{feedId}/comments")
	public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(@LoginUser Long userId,
		@PathVariable Long feedId, @RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(ApiResponse.success(commentService.getComments(feedId, page, size)));
	}

	@PostMapping("/{feedId}/comments")
	public void publishComment(@LoginUser Long userId, @PathVariable Long feedId, @RequestBody ContentRequest request) {
		commentService.publishComment(userId, feedId, request);
	}

	@DeleteMapping("/{commentId}")
	public void deleteComment(@LoginUser Long userId, @PathVariable Long commentId) {
		commentService.deleteComment(userId, commentId);
	}
}
