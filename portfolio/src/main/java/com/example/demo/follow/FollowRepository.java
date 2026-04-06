package com.example.demo.follow;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    long countByFolloweeId(Long followeeId); // フォロワー数
    long countByFollowerId(Long followerId); // フォロー中数
    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}