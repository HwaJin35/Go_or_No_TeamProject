package com.goorno.canigo.unit.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.dto.place.PlaceResponseDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.PlaceRepository;
import com.goorno.canigo.service.PlaceService;

public class PlaceServiceUnitTest {

	private PlaceRepository placeRepository;
	private PlaceService placeService;
	
	@BeforeEach
	void setUp() {
		placeRepository = mock(PlaceRepository.class);
		placeService = new PlaceService(placeRepository);
	}
	
	@Test
	void testCreatePlace() throws IOException {
		// given
		PlaceRequestDTO placeRequestDTO = new PlaceRequestDTO();
		placeRequestDTO.setName("테스트 장소");
		placeRequestDTO.setDescription("테스트 설명");
		placeRequestDTO.setLatitude(36.1234);
		placeRequestDTO.setLongitude(127.5678);
		placeRequestDTO.setFiles(List.of(mock(MultipartFile.class)));
		
		User user = new User();
		user.setId(1L);
		user.setNickname("테스트 유저");
		
		// Base64Util mock
		mockStatic(Base64Util.class);
		when(Base64Util.encodeFilesToBase64(any())).thenReturn(List.of("Base64EncodedString"));
		
		// 저장될 엔티티 mock
		Place savedPlace = new Place();
		savedPlace.setId(100L);
		savedPlace.setName(placeRequestDTO.getName());
		savedPlace.setDescription(placeRequestDTO.getDescription());
		savedPlace.setLatitude(placeRequestDTO.getLatitude());
		savedPlace.setLongitude(placeRequestDTO.getLongitude());
		savedPlace.setUploadFiles(List.of("Base64EncodedString"));
		savedPlace.setUser(user);
		
		when(placeRepository.save(any(Place.class))).thenReturn(savedPlace);
		
		// when
		PlaceResponseDTO result = placeService.createPlace(placeRequestDTO, user);
		
		// then
		assertNotNull(result);
		assertEquals("테스트 장소", result.getName());
		assertEquals("Base64EncodedString", result.getUploadFiles());
		assertEquals(36.1234, result.getLatitude());
		assertEquals(127.5678, result.getLongitude());
		
		verify(placeRepository, times(1)).save(any(Place.class));
	}
}
