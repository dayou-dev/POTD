package com.jhw.potd.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// @Table(indexes = {@Index(name = "idx_feed_created", columnList = "created_at")}) 디비에 쿼리를 통해 반영
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private String imgUrl;

	private String content;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feed")
	private List<Comment> comments = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feed")
	private List<Like> likes = new ArrayList<>();

	@Builder
	public Feed(User user, String imgUrl, String content) {
		this.user = user;
		this.imgUrl = imgUrl;
		this.content = content;
	}
}
