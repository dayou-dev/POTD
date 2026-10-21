package com.jhw.potd.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jhw.potd.domain.Feed;
import com.jhw.potd.domain.User;

import jakarta.persistence.Entity;

public interface FeedRepository extends JpaRepository<Feed, Long> {
	List<Feed> findAllByUser(User user);
	@EntityGraph(attributePaths = {"user"})
	Page<Feed> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
