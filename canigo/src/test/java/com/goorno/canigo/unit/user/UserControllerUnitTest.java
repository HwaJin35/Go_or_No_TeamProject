package com.goorno.canigo.unit.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.goorno.canigo.controller.user.UserController;
import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.mapper.user.UserMapper;
import com.goorno.canigo.service.user.UserDetailsServiceImpl;
import com.goorno.canigo.service.user.UserService;

// UserController 단위 테스트
@WebMvcTest(UserController.class)	// 컨트롤러 레이어만 테스트(Service나 Repository는 로드하지 않음)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화(인증/인가 테스트는 통합 테스트에서 진행, 컨트롤러 기능만 테스트)
public class UserControllerUnitTest {
	// JPA Auditing 기능을 위한 AuditorAware 가짜 객체 등록
	@TestConfiguration
	static class TestConfig {
		@Bean
		public AuditorAware<String> auditorAware() {
			return () -> java.util.Optional.of("testUser");
		}
	}
	
	@Autowired
	private MockMvc mockMvc;	// MockMvc: 실제 서버 띄우지 않고 API 테스트 가능하게 해주는 객체
	
	// Controller가 의존하는 Service를 가짜로 주입
	@MockBean
	private UserService userService;	
	
	@MockBean
	private UserDetailsServiceImpl userDetailsService;	
	
	@MockBean
	private UserMapper userMapper;
	
	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext; // JPA Auditing 관련 의존성 해결용 가짜 등록
	
	private UserRequestDTO requestDTO;
	private UserResponseDTO responseDTO;
	private MockMultipartFile mockFile;
	
	// base64 형식의 프로필 이미지 문자열 세팅
    String profileImageBase64 = "data:image/jpeg;base64,dummybase64string";
	
	@BeforeEach
	void setUp() {
		mockFile = new MockMultipartFile(
				"profileImageFile",
				"test.jpg",
				"image/jepg",
				"mockImageContent".getBytes(StandardCharsets.UTF_8)
				);
				
		// 테스트에 사용할 요청용 DTO 초기화
		requestDTO = UserRequestDTO.builder()
				.email("test@example.com")
				.password("testPassword")
				.nickname("tester")
				.authProvider(AuthProviderType.LOCAL)
				.build();
		requestDTO.setUploadFiles(List.of(mockFile));
		
		// 테스트에 사용할 응답용 DTO 초기화
		responseDTO = UserResponseDTO.builder()
				.id(1L)
				.email("test@example.com")
				.nickname("tester")
				.profileImageFile(profileImageBase64)
				.authProvider(AuthProviderType.LOCAL)
				.status(Status.ACTIVE)
				.build();
		
	}
	
	@Test
	@DisplayName("회원가입 API 성공 테스트")
	void registerUser() throws Exception {
		// given: 가짜 userService가 어떤 UserRequestDTO가 들어와도 responseDTO를 반환하게 설정
		when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);
		
		// when & then :  POST /api/users/signup 요청을 보내고, 응답이 200 OK이며, nickname이 "tester"인지 확인
		mockMvc.perform(multipart("/api/users/signup")
				.file((MockMultipartFile) requestDTO.getUploadFiles().get(0))
				.param("email",  requestDTO.getEmail())
				.param("password",  requestDTO.getPassword())
				.param("nickname", requestDTO.getNickname())
				.param("authProvider",  requestDTO.getAuthProvider().name())
				.contentType("multipart/form-data"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.nickname").value("tester"));
		
		verify(userService).createUser(any(UserRequestDTO.class));
	}
	
	@Test
	@DisplayName("전체 회원 조회 API 성공 테스트")
	void getAllUsers() throws Exception {
		// userService.getAllUsers()가 responseDTO 리스트를 반환하도록 설정
		when(userService.getAllUsers()).thenReturn(Collections.singletonList(responseDTO));
		
		// GET /api/users 요청을 보내고, 첫 번째 회원 nickname이 "tester"인지 확인
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nickname").value("tester"));
	}
	
	@Test
	@DisplayName("이메일로 회원 조회 API 성공 테스트")
	void getUserByEmail() throws Exception {
		when(userService.getUserByEmail("test@example.com")).thenReturn(responseDTO);
		
        // GET /api/users/email/test@example.com 요청을 보내고, nickname이 "tester"인지 확인
		mockMvc.perform(get("/api/users/email/test@example.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nickname").value("tester"));
	}
	
    @Test
    @DisplayName("닉네임으로 회원 조회 API 성공 테스트")
    void getUserByNickname() throws Exception {
        when(userService.getUserByNickname("tester")).thenReturn(responseDTO);

        // GET /api/users/nickname/tester 요청을 보내고, email이 "test@example.com"인지 확인
        mockMvc.perform(get("/api/users/nickname/tester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("회원정보 수정 API 성공 테스트")
    void updateUser() throws Exception {
        when(userService.updateUser(any(Long.class), any(UserUpdateRequestDTO.class))).thenReturn(responseDTO);

        // PUT /api/users/1 요청을 보내고, 수정된 nickname이 "tester"인지 확인
        mockMvc.perform(multipart("/api/users/1")
                .file(mockFile)
                .param("nickname", requestDTO.getNickname())
                .with(request -> { request.setMethod("PUT"); return request; }) // ★ multipart를 PUT으로 바꿔야 함
                .contentType("multipart/form-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("tester"));
    }

    @Test
    @DisplayName("회원 비활성화 API 성공 테스트")
    void deactivateUser() throws Exception {
    	// PATCH /api/users/1/deactivate 요청을 보내고, 상태 코드가 204 No Content인지 확인
        mockMvc.perform(patch("/api/users/1/deactivate"))
               .andExpect(status().isNoContent());
        verify(userService).deactivateUser(1L);
    }

    @Test
    @DisplayName("회원 삭제 API 성공 테스트")
    void deleteUser() throws Exception {
        // DELETE /api/users/1 요청을 보내고, 상태 코드가 204 No Content인지 확인
        mockMvc.perform(delete("/api/users/1"))
               .andExpect(status().isNoContent());
        verify(userService).deleteUser(1L);
    }
}
