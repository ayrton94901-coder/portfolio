package com.example.demo.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // postIdごとのコメント数をまとめて取得
    @Query("""
        select c.post.id, count(c)
        from Comment c
        where c.post.id in :postIds
        group by c.post.id
    """)
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);
    
    @Query("""
    	    select c
    	    from Comment c
    	    join fetch c.user u
    	    where c.post.id = :postId
    	    order by c.createdAt asc
    	""")
    	List<Comment> findByPostIdWithUserOrderByCreatedAtAsc(@Param("postId") Long postId);
}