package com.goorno.canigo.dto.comment;

import com.goorno.canigo.common.dto.CommonMultipartDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO extends CommonMultipartDTO {
	private Long reviewId; // 댓글이 달릴 리뷰ID
	private String content; // 댓글 내용
	private Long parentId; // 대댓글인 경우 부모 댓글 ID(null 가능)
}
