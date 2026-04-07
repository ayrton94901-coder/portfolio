package com.example.demo.like;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	long countByPostId(Long postId);

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

	// ★一覧用：投稿IDのリストを渡して、いいねレコードを全部取る
	List<PostLike> findByPostIdIn(Collection<Long> postIds);

	// ★一覧用：ログインユーザーが押してる分だけ取る
	List<PostLike> findByPostIdInAndUserId(Collection<Long> postIds, Long userId);
}