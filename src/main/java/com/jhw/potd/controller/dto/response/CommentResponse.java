package com.jhw.potd.controller.dto.response;

import com.jhw.potd.domain.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
	private Long userId;
	private String nickname;
	private Long commentId;
	private String content;

	public CommentResponse(Comment comment) {
		this.userId = comment.getUser().getId();
		this.nickname = comment.getUser().getNickname();
		this.commentId = comment.getId();
		this.content = comment.getContent();
	}
}
