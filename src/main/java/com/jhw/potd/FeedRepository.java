package com.jhw.potd;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
	List<Feed> findAllByUser(User user);
}
