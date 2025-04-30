package com.goorno.canigo.dto.place;

import java.util.stream.Collectors;

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

	public static PlaceResponseDTO fromEntity(Place place) {
		PlaceResponseDTO dto = new PlaceResponseDTO();
		dto.setId(place.getId());
		dto.setName(place.getName());
		dto.setDescription(place.getDescription());
		dto.setLatitude(place.getLatitude());
		dto.setLongitude(place.getLongitude());
		dto.setCreatedBy(place.getUser().getNickname()); // 유저 닉네임

		// Join the list of Base64 strings into a single string
		if (place.getUploadFiles() != null && !place.getUploadFiles().isEmpty()) {
			String uploadFilesStr = place.getUploadFiles()
					.stream()
					.collect(Collectors.joining(","));
			dto.setUploadFiles(uploadFilesStr);
		} else {
			dto.setUploadFiles(""); // Empty string if no files
		}

		return dto;
	}
}
