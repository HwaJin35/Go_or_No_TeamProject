package com.goorno.canigo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goorno.canigo.dto.comment.CommentRequestDTO;
import com.goorno.canigo.dto.comment.CommentResponseDTO;
import com.goorno.canigo.entity.Comment;
import com.goorno.canigo.entity.Community;
import com.goorno.canigo.entity.Review;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.CommentTargetType;
import com.goorno.canigo.repository.CommentRepository;
import com.goorno.canigo.repository.CommunityRepository;
import com.goorno.canigo.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final ReviewRepository reviewRepository;
	private final CommunityRepository communityRepository;
	
	public CommentResponseDTO createComment(CommentRequestDTO dto, User user) throws IOException {
		Comment comment = Comment.builder()
				.content(dto.getContent())
				.user(user)
				.targetType(dto.getTargetType())
				.targetId(dto.getTargetId())
				.build();
				
		// 대상이 리뷰일 경우
		if (dto.getTargetType() == CommentTargetType.REVIEW) {
			Review review = reviewRepository.findById(dto.getTargetId())
					.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
			comment.setReview(review);
		}
		
		// 대상이 커뮤니티일 경우
		if (dto.getTargetType() == CommentTargetType.COMMUNITY) {
			Community community = communityRepository.findById(dto.getTargetId())
					.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
			comment.setCommunity(community);
		}
		
		commentRepository.save(comment);
		return CommentResponseDTO.fromEntity(comment);
	}
	
	public List<CommentResponseDTO> getCommentsByReviewId(Long reviewId) {
		return commentRepository.findByReviewId(reviewId)
				.stream()
				.map(CommentResponseDTO::fromEntity)
				.collect(Collectors.toList());
	}
}
