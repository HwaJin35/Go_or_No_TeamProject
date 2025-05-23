package com.goorno.canigo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	// 장소 ID로 찾기
	List<Review> findByPlaceId(Long placeId);
}
