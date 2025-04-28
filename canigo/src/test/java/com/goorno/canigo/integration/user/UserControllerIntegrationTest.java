package com.goorno.canigo.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.repository.UserRepository;

import jakarta.transaction.Transactional;

/**
 * UserController 통합 테스트
 * - 실제 애플리케이션을 띄운 상태에서 Controller ~ Service ~ Repository를 모두 테스트
 * - Mock 객체를 사용하지 않고 실제 동작하는지 검증
 * - DB와 상호작용이 발생하므로 @Transactional로 롤백 처리
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // test 환경으로 properties 세팅
@Transactional
@WithMockUser		// 테스트용 시큐리티 설정, 사용자 인증을 위한 Mock 주입
public class UserControllerIntegrationTest {
	@Autowired
	private UserRepository userRepository;
	
    // 가짜 HTTP 요청을 보내기 위한 객체
    @Autowired
    private MockMvc mockMvc;

    // JAVA 객체를 JSON 문자열로 변환하는 객체
    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile profileImage;

    @BeforeEach
    void setUp() {    	
    	// 테스트 실행 전 DB 비우기
    	userRepository.deleteAll();
    	
    	profileImage = new MockMultipartFile(
        		"files",       
        		"test.jpg",                 // 파일 이름
        		"image/jpeg",               // MIME 타입
        		"test image content".getBytes(StandardCharsets.UTF_8) // 파일 내용
        	);
    }

    @Test
    @DisplayName("회원가입 통합 테스트")
    void registerUser() throws Exception {

        // when - 회원가입 API 호출
    	 mockMvc.perform(multipart("/api/users/signup")
                 .file(profileImage)
                 .param("email", "testuser@example.com")
                 .param("password", "password123")
                 .param("nickname", "testnickname")
                 .param("authProvider", "LOCAL"))
        // then - 정상적으로 회원가입이 되어 200 OK 응답과 함께 nickname 반환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testnickname"));
    }

    @Test
    @DisplayName("전체 회원 조회 통합 테스트")
    void getAllUsers() throws Exception {
        // given - 회원가입
        registerUser();

        // when - 전체 회원 조회 API 호출
        mockMvc.perform(get("/api/users"))

        // then - 등록된 회원이 포함된 리스트 반환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"));
    }

    @Test
    @DisplayName("이메일로 회원 조회 통합 테스트")
    void getUserByEmail() throws Exception {
        // given
    	registerUser();
    	
        // when
        mockMvc.perform(get("/api/users/email/{email}", "testuser@example.com"))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testnickname"));
    }

    @Test
    @DisplayName("닉네임으로 회원 조회 통합 테스트")
    void getUserByNickname() throws Exception {
        // given
    	registerUser();
    	
        // when
        mockMvc.perform(get("/api/users/nickname/{nickname}", "testnickname"))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    @DisplayName("회원 정보 수정 통합 테스트")
    void updateUser() throws Exception {
        // given - 기존 회원가입
        String responseBody = mockMvc.perform(multipart("/api/users/signup")
        		.file(profileImage)
                .param("email", "testuser@example.com")
                .param("password", "password123")
                .param("nickname", "testnickname")
                .param("authProvider", "LOCAL"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

        // responseBody에서 id 파싱
        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        // 수정용 가짜 파일
        MockMultipartFile updatedProfileImage = new MockMultipartFile(
                "files",
                "updated.jpg",
                "image/jpeg",
                "updated image content".getBytes(StandardCharsets.UTF_8)
        );
        
        // when
        mockMvc.perform(multipart("/api/users/{id}", id)
                .file(updatedProfileImage)
                .with(req -> { req.setMethod("PUT"); return req; }) // multipart 기본 POST -> PUT으로 강제 변경
                .param("nickname", "updatedNickname")
            )
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("updatedNickname"));
    }

    @Test
    @DisplayName("회원 비활성화 통합 테스트")
    void deactivateUser() throws Exception {
        // given - 회원가입으로 유저 생성, id 파싱
    	String responseBody = mockMvc.perform(multipart("/api/users/signup")
                .file(profileImage)
                .param("email", "testuser@example.com")
                .param("password", "password123")
                .param("nickname", "testnickname")
                .param("authProvider", "LOCAL"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(responseBody).get("id").asLong();
        
        // when - 해당 ID로 비활성화 API 호출
        mockMvc.perform(patch("/api/users/{id}/deactivate", id))
        // then - 정상적으로 204 No Content가 나오는지 검증
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("회원 삭제 통합 테스트")
    void deleteUser() throws Exception {
    	 // given - 회원가입으로 유저를 생성, id 파싱
    	String responseBody = mockMvc.perform(multipart("/api/users/signup")
                .file(profileImage)
                .param("email", "testuser@example.com")
                .param("password", "password123")
                .param("nickname", "testnickname")
                .param("authProvider", "LOCAL"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        // when - 해당 ID로 삭제 API 호출
        mockMvc.perform(delete("/api/users/{id}", id))
        // then - 정상적으로 204 No Content가 나오는지 검증
                .andExpect(status().isNoContent());
    }
}