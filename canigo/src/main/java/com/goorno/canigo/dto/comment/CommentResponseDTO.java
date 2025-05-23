package com.goorno.canigo.dto.comment;

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
	
	// 엔티티 -> DTO 변환
	public static CommentResponseDTO fromEntity(Comment comment) {
		CommentResponseDTO dto = new CommentResponseDTO();
		dto.setId(comment.getId());
		dto.setContent(comment.getContent());
		dto.setUserNickname(comment.getUser().getNickname());
		dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
		return dto;
	}
}
