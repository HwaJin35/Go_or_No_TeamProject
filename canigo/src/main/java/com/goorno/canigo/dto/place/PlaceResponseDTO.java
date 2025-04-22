package com.goorno.canigo.dto.place;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceResponseDTO {
	private Long id;
	private String name;
	private String descirption;
	private Double latitude;
	private Double longitude;
	private String uploadFile; // DB에서 꺼낸 Base64 이미지
}
