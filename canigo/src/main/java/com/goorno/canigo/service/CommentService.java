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
		
		// 대상이 부모댓글인지 확인
		if (dto.getParentId() != null) {
			Comment parentComment = commentRepository.findById(dto.getParentId())
					.orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
			comment.setParent(parentComment);
		}
		
		commentRepository.save(comment);
		return CommentResponseDTO.fromEntity(comment);
	}
	
	public List<CommentResponseDTO> getCommentsByReviewId(Long reviewId) {
	    // 리뷰에 해당하는 댓글 중 부모 댓글(parent == null)만 조회
	    List<Comment> parentComments = commentRepository.findWithChildrenByReviewId(reviewId);

	    return parentComments.stream()
	        .map(CommentResponseDTO::fromEntity) // 재귀 변환 수행
	        .collect(Collectors.toList());
	}
}
