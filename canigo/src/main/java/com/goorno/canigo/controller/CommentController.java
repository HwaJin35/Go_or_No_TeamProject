package com.goorno.canigo.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.common.util.AuthUtil;
import com.goorno.canigo.dto.comment.CommentRequestDTO;
import com.goorno.canigo.dto.comment.CommentResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final AuthUtil authUtil;
	
	// 댓글 리스트 조회(리뷰별)
	@GetMapping("/review/{reviewId}")
	public ResponseEntity<List<CommentResponseDTO>> getCommentsByReview(@PathVariable("reviewId") Long reviewId) {
		List<CommentResponseDTO> comments = commentService.getCommentsByReviewId(reviewId);
		return ResponseEntity.ok(comments);
	}
	
	// 댓글 등록
	@PostMapping
	public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO dto) {
		User user = authUtil.getCurrentUser();
		
		try {
			CommentResponseDTO response = commentService.createComment(dto, user);
			return ResponseEntity.ok(response);			
		} catch (IOException e) {
			return ResponseEntity.status(500).body(null);
		}
	}
}
