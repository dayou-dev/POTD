package com.jhw.potd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhw.potd.controller.dto.request.ContentRequest;
import com.jhw.potd.controller.dto.response.CommentResponse;
import com.jhw.potd.domain.Comment;
import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.User;
import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;
import com.jhw.potd.repository.CommentRepository;
import com.jhw.potd.repository.FeedRepository;
import com.jhw.potd.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final FeedRepository feedRepository;
	private final UserRepository userRepository;

	@Transactional
	public void publishComment(Long userId, Long feedId, ContentRequest contentRequest) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
			new CustomException(ErrorCode.FEED_NOT_FOUND));
		Comment comment = Comment.builder().user(user).feed(feed).content(contentRequest.getContent()).build();
		commentRepository.save(comment);
	}

	@Transactional
	public void deleteComment(Long userId, Long commentId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
		if (!comment.getUser().equals(user) && !comment.getFeed().getUser().equals(user)) {
			throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
		}
		commentRepository.delete(comment);
	}

	@Transactional(readOnly = true)
	public Page<CommentResponse> getComments(Long feedId, int page, int size) {

		Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
			new CustomException(ErrorCode.FEED_NOT_FOUND));
		Pageable pageable = PageRequest.of(page, size);
		Page<Comment> comments = commentRepository.findAllByFeed(feed, pageable);
		return comments.map(CommentResponse::new);
	}
}
