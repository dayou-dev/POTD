package com.jhw.potd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.Like;
import com.jhw.potd.domain.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByUserAndFeed(User user, Feed feed);

	void deleteByUserAndFeed(User user, Feed feed);
}
