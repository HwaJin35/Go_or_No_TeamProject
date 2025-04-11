package com.goorno.canigo.dto;

import com.goorno.canigo.common.dto.CommonMultipartDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceMultipartDTO extends CommonMultipartDTO {
	private String name; // 장소 이름
	private String description; // 장소 설명
	private Double latitude; // 위도
	private Double longitude; // 경도
}

// 장소 등록에 필요한 정보를 담는 DTO
// PlaceController에서 @ModelAttribute로 이 클래스를 받음
