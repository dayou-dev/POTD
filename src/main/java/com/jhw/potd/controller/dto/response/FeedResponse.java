package com.jhw.potd.controller.dto.response;

import com.jhw.potd.domain.Feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponse {
	private Long userId;
	private Long feedId;
	private String imgUrl;
	private String content;

	public FeedResponse(Feed feed) {
		this.userId = feed.getUser().getId();
		this.feedId = feed.getId();
		this.imgUrl = feed.getImgUrl();
		this.content = feed.getContent();
	}
}
