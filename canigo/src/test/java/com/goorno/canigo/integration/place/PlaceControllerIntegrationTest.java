package com.goorno.canigo.integration.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.goorno.canigo.controller.PlaceController;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.dto.place.PlaceResponseDTO;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.PlaceService;
import com.goorno.canigo.test.DataLoader;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PlaceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PlaceService placeService;

    @MockBean
    private DataLoader dataLoader;

    @InjectMocks
    private PlaceController placeController;

    private PlaceRequestDTO placeRequestDTO;
    private PlaceResponseDTO placeResponseDTO;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        // 더미 사용자의 nickname을 고정된 값으로 설정
        dummyUser = new User();
        dummyUser.setNickname("dummyUser");  // 고정된 값으로 설정
        System.out.println("Dummy User Nickname: " + dummyUser.getNickname());  // 디버깅 출력
        
        // 더미 사용자 DB에 저장
        userRepository.save(dummyUser);

        placeRequestDTO = new PlaceRequestDTO();
        placeRequestDTO.setName("Test Place");
        placeRequestDTO.setDescription("Test Description");
        placeRequestDTO.setLatitude(37.5665);
        placeRequestDTO.setLongitude(126.9780);

        placeResponseDTO = new PlaceResponseDTO();
        placeResponseDTO.setId(1L);
        placeResponseDTO.setName("Test Place");
        placeResponseDTO.setDescription("Test Description");
        placeResponseDTO.setLatitude(37.5665);
        placeResponseDTO.setLongitude(126.9780);
        placeResponseDTO.setCreatedBy(dummyUser.getNickname());  // dummyUser로 설정
        System.out.println("CreatedBy: " + placeResponseDTO.getCreatedBy()); // 디버깅 출력
        placeResponseDTO.setUploadFiles("");
    }

    @Test
    void testRegisterPlace() throws Exception {
        // placeResponseDTO의 createdBy 값을 명확하게 설정합니다.
        placeResponseDTO.setCreatedBy(dummyUser.getNickname());
        
        when(placeService.createPlace(any(), any())).thenReturn(placeResponseDTO);

        mockMvc.perform(multipart("/api/places")
                        .file("file", "dummy file content".getBytes())
                        .param("name", placeRequestDTO.getName())
                        .param("description", placeRequestDTO.getDescription())
                        .param("latitude", String.valueOf(placeRequestDTO.getLatitude()))
                        .param("longitude", String.valueOf(placeRequestDTO.getLongitude())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeResponseDTO.getId()))
                .andExpect(jsonPath("$.name").value(placeResponseDTO.getName()))
                .andExpect(jsonPath("$.description").value(placeResponseDTO.getDescription()))
                .andExpect(jsonPath("$.latitude").value(placeResponseDTO.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(placeResponseDTO.getLongitude()))
                .andExpect(jsonPath("$.createdBy").value(placeResponseDTO.getCreatedBy()))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString())); // 응답 본문 출력
    }

    @Test
    void testGetAllPlaces() throws Exception {
    	Place place = new Place();
        place.setId(1L);
        place.setName("Test Place");
        place.setDescription("Test Description");
        place.setLatitude(37.5665);
        place.setLongitude(126.9780);
        place.setUser(dummyUser);

        // Place 엔티티 리스트 생성
        when(placeService.getAllPlaces()).thenReturn(List.of(place));

        mockMvc.perform(get("/api/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(place.getId()))
                .andExpect(jsonPath("$[0].name").value(place.getName()))
                .andExpect(jsonPath("$[0].description").value(place.getDescription()))
                .andExpect(jsonPath("$[0].latitude").value(place.getLatitude()))
                .andExpect(jsonPath("$[0].longitude").value(place.getLongitude()))
                .andExpect(jsonPath("$[0].createdBy").value(place.getUser().getNickname()));
    }

    @Test
    void testGetPlaceById() throws Exception {
        // placeResponseDTO 설정
        placeResponseDTO.setId(1L);
        placeResponseDTO.setName("Test Place");
        placeResponseDTO.setDescription("Test Description");
        placeResponseDTO.setLatitude(37.5665);
        placeResponseDTO.setLongitude(126.9780);
        placeResponseDTO.setCreatedBy(dummyUser.getNickname());

        // ArgumentCaptor 설정
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        // Mocking 및 응답 준비
        when(placeService.getPlace(1L)).thenReturn(placeResponseDTO);

        // 실제 API 호출
        mockMvc.perform(get("/api/places/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeResponseDTO.getId()))
                .andExpect(jsonPath("$.name").value(placeResponseDTO.getName()))
                .andExpect(jsonPath("$.description").value(placeResponseDTO.getDescription()))
                .andExpect(jsonPath("$.latitude").value(placeResponseDTO.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(placeResponseDTO.getLongitude()))
                .andExpect(jsonPath("$.createdBy").value(placeResponseDTO.getCreatedBy()));

        // ArgumentCaptor로 getPlace() 메서드 호출 시 전달된 인자 확인
        verify(placeService).getPlace(captor.capture());
        Long capturedId = captor.getValue();
        assertEquals(1L, capturedId);  // 1L이 캡처되었는지 확인
    }
}