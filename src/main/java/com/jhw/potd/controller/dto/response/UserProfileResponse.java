package com.jhw.potd.controller.dto.response;

import java.util.List;

import com.jhw.potd.domain.Feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
	private Long userId;
	private String nickname;
	private List<FeedResponse> feeds;
}
