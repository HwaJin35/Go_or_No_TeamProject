package com.goorno.canigo.dto.place;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goorno.canigo.common.dto.CommonMultipartDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceRequestDTO extends CommonMultipartDTO {
	private String name; // 장소 이름
	private String description; // 장소 설명
	private Double latitude; // 위도
	private Double longitude; // 경도
	
	@JsonProperty("category") // JSON 요청의 'category'가 이 필드에 매핑
	private String categoryName; // 카테고리
	private List<String> hashtags;
}

// 장소 등록에 필요한 정보를 담는 DTO
// PlaceController에서 @ModelAttribute로 이 클래스를 받음
