package com.jhw.potd;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequest {

	private String imgUrl;
	private String content;
}
