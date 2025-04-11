package com.goorno.canigo.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.goorno.canigo.dto.PlaceMultipartDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	
	private final PlaceRepository placeRepository;
	
	public Place savePlace(PlaceMultipartDTO dto) throws IOException {
		// 저정할 디렉토리
		String uploadDir = "/upload/";
		String imageUrl = null;
		
		MultipartFile image = dto.getImage();
		if (image != null && !image.isEmpty()) {
			String filename = UUID.randomUUID() + "_" + dto.getImage().getOriginalFilename();
			File file = new File(uploadDir + filename);
			
			// 디렉토리 없으면 생성
			File dir = new File(uploadDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			image.transferTo(file);
			imageUrl = "/upload/" + filename;
			
		}
		
		Place place = new Place();
		place.setName(dto.getName());
		place.setDescription(dto.getDescription());
		place.setLatitude(dto.getLatitude());
		place.setLongitude(dto.getLongitude());
		place.setImageUrl(imageUrl); // null이어도 OK

		return placeRepository.save(place);
	}
	
	public List<Place> getAllPlaces() {
		return placeRepository.findAll();
	}
}
