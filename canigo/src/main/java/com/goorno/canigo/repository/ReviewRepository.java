package com.goorno.canigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
}
