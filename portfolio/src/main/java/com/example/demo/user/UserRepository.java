package com.example.demo.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username); // ←変更

	boolean existsByUsername(String username); // ←変更

	boolean existsByHandle(String handle);
}