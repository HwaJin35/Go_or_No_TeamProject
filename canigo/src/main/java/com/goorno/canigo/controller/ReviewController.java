package com.goorno.canigo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.common.util.AuthUtil;
import com.goorno.canigo.dto.review.ReviewRequestDTO;
import com.goorno.canigo.dto.review.ReviewResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final AuthUtil authUtil;
	
	// 리뷰 리스트 조회(장소별)
	@GetMapping("/place/{placeId}")
	public ResponseEntity<List<ReviewResponseDTO>> getReviewByPlace(@PathVariable("placeId") Long placeId) {
		List<ReviewResponseDTO> reviews = reviewService.getReviewsByPlaceId(placeId);
		return ResponseEntity.ok(reviews);
	}
	
	// 리뷰 등록
	@PostMapping(consumes = {"multipart/form-data"})
	public ResponseEntity<ReviewResponseDTO> createReview(@ModelAttribute ReviewRequestDTO dto) {
		// 유저 정보는 실제 인증된 유저로 처리
		User user = authUtil.getCurrentUser();
		
		try {
			ReviewResponseDTO response = reviewService.createReview(dto, user);
			return ResponseEntity.ok(response);			
		} catch (IOException e) {
			return ResponseEntity.status(500).body(null);
		}
	}
	
}
