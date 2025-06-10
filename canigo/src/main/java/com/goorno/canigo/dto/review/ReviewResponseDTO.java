package com.goorno.canigo.dto.review;

import java.time.LocalDateTime;
import java.util.List;

import com.goorno.canigo.entity.Review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDTO {
	private Long id;
	private String title;
	private String content;
	private List<String> uploadFiles; // 이미지 URL 리스트
	private String userNickname;	// 작성자 닉네임(간단 노출용)
	private LocalDateTime createdAt;
	
	// 엔티티 -> DTO 변환 메서드
	public static ReviewResponseDTO fromEntity(Review review) {
		ReviewResponseDTO dto = new ReviewResponseDTO();
		dto.setId(review.getId());
		dto.setTitle(review.getTitle());
		dto.setContent(review.getContent());
		dto.setUploadFiles(review.getUploadFiles() != null ? review.getUploadFiles() : List.of());
		dto.setUserNickname(review.getUser().getNickname());
		dto.setCreatedAt(review.getCreatedAt());
		return dto;
	}
}
