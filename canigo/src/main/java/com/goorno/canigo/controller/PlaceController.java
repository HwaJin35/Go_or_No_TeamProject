package com.goorno.canigo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.dto.PlaceMultipartDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.service.PlaceService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:8080")
@RestController // 모든 메서드는 자동으로 @ReponseBody가 적용되어 JSON으로 반환
@RequiredArgsConstructor // final로 선언된 필드를 자동으로 생성자 주입
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;
	
	// 클라이언트에서 multipart/form-data 형식으로 보낼 때만 이 메서드가 실행됨
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> registerPlace(@ModelAttribute PlaceMultipartDTO dto) {
		try {
			Place place = placeService.savePlace(dto);
			return ResponseEntity.ok(place);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
		}
	}
	
	@GetMapping
	public List<Place> getAllPlaces(){
		return placeService.getAllPlaces();
	}
}
