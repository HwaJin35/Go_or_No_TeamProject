package com.goorno.canigo.dto.review;

import com.goorno.canigo.common.dto.CommonMultipartDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO extends CommonMultipartDTO {
	private Long placeId; // 리뷰가 달릴 장소 ID
	private String title; // 리뷰 제목
	private String content; // 리뷰 내용

}
