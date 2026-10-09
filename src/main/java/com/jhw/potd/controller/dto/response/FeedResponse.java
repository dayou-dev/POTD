package com.jhw.potd.controller.dto.response;

import java.time.LocalDateTime;

import com.jhw.potd.domain.Feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponse {
	private Long userId;
	private String nickname;
	private Long feedId;
	private String imgUrl;
	private String content;
	private long likeCount;
	private long commentCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public FeedResponse(Feed feed) {
		this.userId = feed.getUser().getId();
		this.nickname = feed.getUser().getNickname();
		this.feedId = feed.getId();
		this.imgUrl = feed.getImgUrl();
		this.content = feed.getContent();
		this.commentCount = feed.getComments().size();
		this.likeCount = feed.getLikes().size();
		this.createdAt = feed.getCreatedAt();
		this.updatedAt = feed.getUpdatedAt();
	}
}
