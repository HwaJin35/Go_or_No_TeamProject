package com.goorno.canigo.unit.controller.place;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.goorno.canigo.controller.PlaceController;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.dto.place.PlaceResponseDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.service.PlaceService;
import com.goorno.canigo.test.DataLoader;

public class PlaceControllerUnitTest {

	@Mock
	private PlaceService placeService;

	@Mock
	private DataLoader dataLoader;

	private PlaceController placeController;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		placeController = new PlaceController(placeService, dataLoader);
		mockMvc = MockMvcBuilders.standaloneSetup(placeController).build();
	}

	// 장소 등록 테스트
	@Test
	void registerPlace_shouldReturnCreatedPlace() throws Exception {
		// Given
		PlaceRequestDTO requestDTO = new PlaceRequestDTO();
		requestDTO.setName("Test Place");
		requestDTO.setDescription("A nice place");
		requestDTO.setLatitude(37.5665);
		requestDTO.setLongitude(126.9780);

		User dummyUser = new User();
		dummyUser.setNickname("testUser"); // 가상의 사용자 닉네임 설정
		when(dataLoader.getDummyUser()).thenReturn(dummyUser);

		PlaceResponseDTO placeResponseDTO = new PlaceResponseDTO();
		placeResponseDTO.setId(1L);
		placeResponseDTO.setName("Test Place");
		placeResponseDTO.setDescription("A nice place");
		placeResponseDTO.setLatitude(37.5665);
		placeResponseDTO.setLongitude(126.9780);
		placeResponseDTO.setCreatedBy("testUser"); // 생성자 닉네임 설정
		placeResponseDTO.setUploadFiles("base64ImageString"); // Base64 이미지 문자열 설정

		when(placeService.createPlace(any(), eq(dummyUser))).thenReturn(placeResponseDTO);

		// When & Then
		mockMvc.perform(post("/api/places").contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("name", "Test Place")
				.param("description", "A nice place").param("latitude", "37.5665").param("longitude", "126.9780"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Test Place"))
				.andExpect(jsonPath("$.description").value("A nice place"))
				.andExpect(jsonPath("$.latitude").value(37.5665)).andExpect(jsonPath("$.longitude").value(126.9780))
				.andExpect(jsonPath("$.createdBy").value("testUser")) // createdBy 값 검증
				.andExpect(jsonPath("$.uploadFiles").value("base64ImageString")); // uploadFile 값 검증
	}

	@Test
	void getAllPlaces_shouldReturnListOfPlaces() throws Exception {
	    // Given
	    Place place1 = new Place();
	    place1.setId(1L);
	    place1.setName("Place 1");
	    place1.setDescription("Description 1");
	    place1.setLatitude(37.5665);
	    place1.setLongitude(126.9780);
	    place1.setUploadFiles(Arrays.asList("base64ImageString")); // 수정된 부분: 실제 base64 문자열 설정
	    User user1 = new User();
	    user1.setNickname("user1");
	    place1.setUser(user1);

	    Place place2 = new Place();
	    place2.setId(2L);
	    place2.setName("Place 2");
	    place2.setDescription("Description 2");
	    place2.setLatitude(35.1796);
	    place2.setLongitude(129.0756);
	    place2.setUploadFiles(Arrays.asList("base64ImageString"));
	    User user2 = new User();
	    user2.setNickname("user2");
	    place2.setUser(user2);

	    List<Place> places = Arrays.asList(place1, place2);
	    List<PlaceResponseDTO> placeResponseDTOs = places.stream()
	        .map(place -> PlaceResponseDTO.fromEntity(place)) // 변환 메서드 사용
	        .collect(Collectors.toList());

	    when(placeService.getAllPlaces()).thenReturn(places);

	    // When & Then
	    mockMvc.perform(get("/api/places"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].name").value("Place 1"))
	        .andExpect(jsonPath("$[0].createdBy").value("user1"))
	        .andExpect(jsonPath("$[0].uploadFiles").value("base64ImageString"))
	        .andExpect(jsonPath("$[1].name").value("Place 2"))
	        .andExpect(jsonPath("$[1].createdBy").value("user2"))
	        .andExpect(jsonPath("$[1].uploadFiles").value("base64ImageString"));
	}

	// 특정 장소 조회 테스트
	@Test
	void getPlace_shouldReturnPlace() throws Exception {
		// Given
		PlaceResponseDTO placeResponseDTO = new PlaceResponseDTO();
		placeResponseDTO.setId(1L);
		placeResponseDTO.setName("Test Place");
		placeResponseDTO.setDescription("A nice place");
		placeResponseDTO.setLatitude(37.5665);
		placeResponseDTO.setLongitude(126.9780);
		placeResponseDTO.setCreatedBy("testUser");
		placeResponseDTO.setUploadFiles("base64ImageString");

		when(placeService.getPlace(1L)).thenReturn(placeResponseDTO);

		// When & Then
		mockMvc.perform(get("/api/places/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test Place"))
				.andExpect(jsonPath("$.description").value("A nice place"))
				.andExpect(jsonPath("$.latitude").value(37.5665))
				.andExpect(jsonPath("$.longitude").value(126.9780))
				.andExpect(jsonPath("$.createdBy").value("testUser"))
				.andExpect(jsonPath("$.uploadFiles").value("base64ImageString"));
	}
}