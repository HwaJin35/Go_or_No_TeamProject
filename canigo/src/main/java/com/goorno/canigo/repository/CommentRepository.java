package com.goorno.canigo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByReviewId(Long reviewId);
}
