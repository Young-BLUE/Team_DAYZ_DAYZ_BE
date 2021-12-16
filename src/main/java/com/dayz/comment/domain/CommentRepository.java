package com.dayz.comment.domain;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select distinct c from Comment c where c.post.id = :postId")
    List<Comment> findAllByPost(@Param("postId") Long postId);

    @Query("select c from Comment c where c.post.id = :postId")
    Page<Comment> findAllByPostId(@Param("postId") Long postId, Pageable pageable);
}