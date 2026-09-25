package com.jhw.potd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhw.potd.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailAndPassword(String email, String password);

	boolean existsByEmail(String email);
}
