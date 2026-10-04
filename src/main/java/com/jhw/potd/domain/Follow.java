package com.jhw.potd.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Follow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 나를 팔로워 - 주체
	@JoinColumn(name = "follower_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User follower;

	// 내가 팔로우 - 대상
	@JoinColumn(name = "following_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User following;

	@Builder
	public Follow(User follower, User following) {
		this.follower = follower;
		this.following = following;
	}
}
