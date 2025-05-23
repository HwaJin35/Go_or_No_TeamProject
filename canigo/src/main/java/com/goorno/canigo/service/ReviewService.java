package com.goorno.canigo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.dto.review.ReviewRequestDTO;
import com.goorno.canigo.dto.review.ReviewResponseDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.Review;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.PlaceRepository;
import com.goorno.canigo.repository.ReviewRepository;
import com.goorno.canigo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;
	
	public List<ReviewResponseDTO> getReviewsByPlaceId(Long placeId) {
		return reviewRepository.findByPlaceId(placeId)
				.stream()
				.map(ReviewResponseDTO::fromEntity)
				.collect(Collectors.toList());
	}
	
	public ReviewResponseDTO createReview(ReviewRequestDTO dto, User user) throws IOException {
		Place place = placeRepository.findById(dto.getPlaceId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
		
		List<String> base64Files = Base64Util.encodeFilesToBase64(dto.getUploadFiles());
		
		Review review = Review.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.uploadFiles(base64Files)
				.place(place)
				.user(user)
				.build();
		
		reviewRepository.save(review);
		return ReviewResponseDTO.fromEntity(review);
	}
}
