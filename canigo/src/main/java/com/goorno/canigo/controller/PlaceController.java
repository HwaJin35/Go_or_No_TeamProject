package com.goorno.canigo.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.common.util.AuthUtil;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.dto.place.PlaceResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.CategoryType;
import com.goorno.canigo.service.PlaceService;
import com.goorno.canigo.test.DataLoader;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:8080")
@RestController // 모든 메서드는 자동으로 @ReponseBody가 적용되어 JSON으로 반환
@RequiredArgsConstructor // final로 선언된 필드를 자동으로 생성자 주입
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;
	private final DataLoader dataLoader;
	private final AuthUtil authUtil;
	
	// 장소 등록 API
	// 클라이언트에서 multipart/form-data 형식으로 보낼 때만 이 메서드가 실행됨
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> registerPlace(@ModelAttribute PlaceRequestDTO dto) {
		try {
			// 유저 정보는 실제 요청에서 받아야 한다.
			// 여기서는 임시로 user를 받아온다고 가정
//			User user = dataLoader.getDummyUser();
			
			User user = authUtil.getCurrentUser();
			
			// 장소 등록 서비스 호출
			PlaceResponseDTO placeResponseDTO = placeService.createPlace(dto, user);
			
			// 응답을 DTO 형태로 반환
			return ResponseEntity.ok(placeResponseDTO);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
		}
	}
	
	// 모든 장소 조회 API
	@GetMapping
	public ResponseEntity<List<PlaceResponseDTO>> getAllPalces() {
		List<PlaceResponseDTO> places = placeService.getAllPlaces().stream()
				.map(PlaceResponseDTO::fromEntity) // 변환 메서드 필요
				.collect(Collectors.toList());
		return ResponseEntity.ok(places);
	}
	
	// 장소 조회 API
	@GetMapping("/{id}")
	public ResponseEntity<PlaceResponseDTO> getPlace(@PathVariable("id") Long id) {
		PlaceResponseDTO placeResponseDTO = placeService.getPlace(id);
		return ResponseEntity.ok(placeResponseDTO);
	}
	
	// 장소에 대한 카테고리 목록 제공하기
	@GetMapping("/categories")
	public List<String> getCategories() {
		return Arrays.stream(CategoryType.values())
				.map(CategoryType::getCategoryName)
				.collect(Collectors.toList());
	}
	
	// 장소 수정 API
	@PutMapping("/{id}")
	public ResponseEntity<PlaceResponseDTO> updatePlace(@PathVariable("id") Long id, @RequestBody PlaceRequestDTO dto) {
		try {
			User user = authUtil.getCurrentUser();
			PlaceResponseDTO updated = placeService.updatePlace(id, dto, user);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
