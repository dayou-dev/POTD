package com.jhw.potd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.User;

public interface FeedRepository extends JpaRepository<Feed, Long> {
	List<Feed> findAllByUser(User user);
}
