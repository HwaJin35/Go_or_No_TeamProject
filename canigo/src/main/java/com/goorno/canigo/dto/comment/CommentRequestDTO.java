package com.goorno.canigo.dto.comment;

import com.goorno.canigo.common.dto.CommonMultipartDTO;
import com.goorno.canigo.entity.enums.CommentTargetType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO extends CommonMultipartDTO {
	private Long targetId; // 댓글이 달릴 대상 ID(리뷰 ID, 커뮤니티 ID)
	private CommentTargetType targetType; // 대상 타입(REVIEW, COMMUNITY)
	private String content; // 댓글 내용
	private Long parentId; // 대댓글인 경우 부모 댓글 ID(null 가능)
}
