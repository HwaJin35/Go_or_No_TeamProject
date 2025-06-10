package com.goorno.canigo.dto.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.goorno.canigo.entity.Comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {
	private Long id;
	private String content;
	private String userNickname; // 작성자 닉네임
	private Long parentId;	// 대댓글 여부
	private LocalDateTime createdAt; // 등록일시
	private List<CommentResponseDTO> children = new ArrayList<>(); // 자식 댓글 리스트
	
	// 엔티티 -> DTO 변환
	public static CommentResponseDTO fromEntity(Comment comment) {
		CommentResponseDTO dto = new CommentResponseDTO();
		dto.setId(comment.getId());
		dto.setContent(comment.getContent());
		dto.setUserNickname(comment.getUser().getNickname());
		dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
		dto.setCreatedAt(comment.getCreatedAt());
		
		// 자식 댓글 재귀 변환
		for (Comment child : Optional.ofNullable(comment.getChildren()).orElse(Collections.emptyList())) {
		    dto.getChildren().add(fromEntity(child));
		}
		
		return dto;
	}
}
