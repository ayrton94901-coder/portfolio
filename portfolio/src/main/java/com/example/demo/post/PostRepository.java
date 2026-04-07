package com.example.demo.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	// 追加：一覧（ユーザーも一緒に取る）
	@Query("""
			    select distinct p
			    from Post p
			    join fetch p.user
			    left join fetch p.tags t
			    order by p.createdAt desc
			""")
	List<Post> findAllWithUserOrderByCreatedAtDesc();

	// 既存：感想（caption）だけ検索（残してOK）
	List<Post> findByCaptionContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

	Optional<Post> findByIdAndUserId(Long id, Long userId);

	List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

	// 変更：search に join fetch p.user を追加
	@Query("""
			    select distinct p
			    from Post p
			    join fetch p.user
			    left join fetch p.tags t
			    where
			        lower(p.movieTitle) like lower(concat('%', :q, '%'))
			        or lower(p.director) like lower(concat('%', :q, '%'))
			        or lower(p.caption) like lower(concat('%', :q, '%'))
			        or lower(t.name) like lower(concat('%', :q, '%'))
			    order by p.createdAt desc
			""")
	List<Post> search(@Param("q") String q);

	// 変更：findByTag に join fetch p.user を追加
	@Query("""
			    select distinct p
			    from Post p
			    join fetch p.user
			    join fetch p.tags t
			    where lower(t.name) = lower(:tag)
			    order by p.createdAt desc
			""")
	List<Post> findByTag(@Param("tag") String tag);

	@Query("""
			    select p
			    from Post p
			    join fetch p.user
			    left join fetch p.tags
			    where p.id = :id
			""")
	Optional<Post> findByIdWithUserAndTags(@Param("id") Long id);

	@Query("""
			    select distinct p
			    from Post p
			    join fetch p.user
			    left join fetch p.tags t
			    where p.user.id = :userId
			    order by p.createdAt desc
			""")
	List<Post> findByUserIdWithUserAndTagsOrderByCreatedAtDesc(@Param("userId") Long userId);
}