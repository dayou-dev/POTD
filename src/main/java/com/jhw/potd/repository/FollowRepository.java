package com.jhw.potd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhw.potd.domain.Follow;
import com.jhw.potd.domain.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByFollowerAndFollowing(User me, User target);

	void deleteByFollowerAndFollowing(User me, User target);
}
