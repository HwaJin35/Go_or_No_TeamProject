package com.goorno.canigo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goorno.canigo.dto.comment.CommentRequestDTO;
import com.goorno.canigo.dto.comment.CommentResponseDTO;
import com.goorno.canigo.entity.Comment;
import com.goorno.canigo.entity.Review;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.CommentRepository;
import com.goorno.canigo.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final ReviewRepository reviewRepository;
	
	public CommentResponseDTO createComment(CommentRequestDTO dto, User user) throws IOException {
		Review review = reviewRepository.findById(dto.getReviewId())
				.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
		
		Comment comment = Comment.builder()
				.content(dto.getContent())
				.user(user)
				.review(review)
				.build();
		
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
