package com.goorno.canigo.dto.place;

import java.util.List;
import java.util.stream.Collectors;

import com.goorno.canigo.entity.Hashtag;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.enums.CategoryType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceResponseDTO {
	private Long id;
	private String name;
	private String description;
	private Double latitude;
	private Double longitude;
	private String createdBy; // 유저 닉네임
	private CategoryType category;
	private String uploadFiles; // DB에서 꺼낸 Base64 이미지
	private List<String> hashtags;

	public static PlaceResponseDTO fromEntity(Place place) {
		PlaceResponseDTO dto = new PlaceResponseDTO();
		dto.setId(place.getId());
		dto.setName(place.getName());
		dto.setDescription(place.getDescription());
		dto.setLatitude(place.getLatitude());
		dto.setLongitude(place.getLongitude());
		dto.setCreatedBy(place.getUser().getNickname()); // 유저 닉네임

		
		if (place.getUploadFiles() != null && !place.getUploadFiles().isEmpty()) {
			String uploadFilesStr = place.getUploadFiles()
					.stream()
					.collect(Collectors.joining(","));
			dto.setUploadFiles(uploadFilesStr);
		} else {
			dto.setUploadFiles(""); 
		}
		
		dto.setCategory(place.getCategory());
		
		// 해시태그 이름만 추출해서 세팅
		dto.setHashtags(
				place.getHashtags()
					.stream()
					.map(Hashtag::getName)
					.collect(Collectors.toList())
				);

		return dto;
	}
}
