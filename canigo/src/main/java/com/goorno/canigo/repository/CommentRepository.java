package com.goorno.canigo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goorno.canigo.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByReviewId(Long reviewId);

	// LAZY 로딩일 때, 반복적으로 DB를 치는 것을 방지
	@Query("SELECT c FROM Comment c LEFT JOIN FETCH c.children WHERE c.review.id = :reviewId AND c.parent IS NULL")
	List<Comment> findWithChildrenByReviewId(@Param("reviewId") Long reviewId);
}
