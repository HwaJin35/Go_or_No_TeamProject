package com.goorno.canigo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.dto.place.PlaceResponseDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final로 선언된 필드를 자동으로 생성자 주입
public class PlaceService {
	
	private final PlaceRepository placeRepository;
	
	// 장소 등록 메서드
	public PlaceResponseDTO createPlace(PlaceRequestDTO placeRequestDTO, User user) throws IOException {
		// 업로드된 파일을 Base64로 변환
		List<String> base64Files = Base64Util.encodeFilesToBase64(placeRequestDTO.getFiles());
		
		// Place 엔티티 생성
		Place place = new Place();
		place.setName(placeRequestDTO.getName());
		place.setDescription(placeRequestDTO.getDescription());
		place.setLatitude(placeRequestDTO.getLatitude());
		place.setLongitude(placeRequestDTO.getLongitude());
		place.setUser(user); // 등록한 유저
		place.setUploadFiles(base64Files);
		
		// 장소를 DB에 저장
		Place savedPlace = placeRepository.save(place);
		
		// 응답 DTO로 변환하여 반환
		PlaceResponseDTO placeResponseDTO = mapToResponseDTO(savedPlace);
		
		placeResponseDTO.setCreatedBy(user.getNickname());
		
		return placeResponseDTO;
	}
	
	// 모든 장소 조회 메서드
	public List<Place> getAllPlaces() {
		return placeRepository.findAll();
	}
	
	// 장소 조회
	public PlaceResponseDTO getPlace(Long id) {
		Place place = placeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("장소를 찾을 수 없습니다. id: " + id));
		return mapToResponseDTO(place);
	}
	
	// DTO로 변환
	private PlaceResponseDTO mapToResponseDTO(Place place) {
		PlaceResponseDTO placeResponseDTO = new PlaceResponseDTO();
		placeResponseDTO.setId(place.getId());
		placeResponseDTO.setName(place.getName());
		placeResponseDTO.setDescription(place.getDescription());
		placeResponseDTO.setLatitude(place.getLatitude());
		placeResponseDTO.setLongitude(place.getLongitude());
		
		// 첫 번째 파일을 응답
		placeResponseDTO.setUploadFiles(place.getUploadFiles().isEmpty() 
				? null : place.getUploadFiles().get(0));
		
		return placeResponseDTO;
	}
}
