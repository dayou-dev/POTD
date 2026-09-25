package com.jhw.potd;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
	private Long userId;
	private String nickname;
	private List<Feed> feeds;
}
