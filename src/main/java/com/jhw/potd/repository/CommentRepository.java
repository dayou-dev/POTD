package com.jhw.potd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jhw.potd.domain.Comment;
import com.jhw.potd.domain.Feed;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Page<Comment> findAllByFeed(Feed feed,  Pageable pageable);

}
