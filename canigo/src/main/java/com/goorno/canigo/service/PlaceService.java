package com.goorno.canigo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	
	private final PlaceRepository placeRepository;
	
	
	public Place savePlace(PlaceRequestDTO dto) throws IOException {
		Place place = new Place();
		place.setName(dto.getName());
		place.setDescription(dto.getDescription());
		place.setLatitude(dto.getLatitude());
		place.setLongitude(dto.getLongitude());
		
		// 업로드 파일을 Base64로 변환하는 메서드 호출
		List<String> uploadFiles = Base64Util.encodeFilesToBase64(dto.getFiles());
		place.setUploadFiles(uploadFiles); // 다중 파일 저장
		

		return placeRepository.save(place);
	}
	
	public List<Place> getAllPlaces() {
		return placeRepository.findAll();
	}
}
