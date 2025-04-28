package com.goorno.canigo.integration.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.dto.login.LoginRequestDTO;

// LoginController 통합테스트
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private LoginRequestDTO loginRequestDTO;
	private String testEmail;
	private String testPassword;
	private String testNickname;
	private MockMultipartFile profileImage;
	
	
	@BeforeEach
    void setUp() throws Exception {
		// 공통 데이터 설정
	    testEmail = "test" + System.currentTimeMillis() + "@example.com"; // 시간 기반으로 다른 이메일 생성!
	    testPassword = "testPassword";
	    testNickname = "tester" + System.currentTimeMillis(); // 닉네임 각각 생성

	    loginRequestDTO = LoginRequestDTO.builder()
	            .email(testEmail)
	            .password(testPassword)
	            .build();

	    profileImage = new MockMultipartFile(
	            "files",
	            "test.jpg",
	            "image/jpeg",
	            "test image content".getBytes(StandardCharsets.UTF_8)
	    );

	    // 매 테스트 시작 전에 회원가입
	    mockMvc.perform(multipart("/api/users/signup")
	            .file(profileImage)
	            .param("email", testEmail)
	            .param("password", testPassword)
	            .param("nickname", testNickname)
	            .param("authProvider", "LOCAL"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.nickname").value(testNickname));
    }
	
	@Test
	@DisplayName("로그인 성공 통합 테스트")
	void login_success() throws Exception {
		
		mockMvc.perform(post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists());
	}
	
	@Test
	@DisplayName("로그인 실패_이메일 없음")
	void login_fail_emailNotFound() throws Exception {
	    LoginRequestDTO invalidEmailRequestDTO = LoginRequestDTO.builder()
	            .email("nonexistent@example.com")
	            .password("wrongPassword")
	            .build();
		
		mockMvc.perform(post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidEmailRequestDTO)))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() throws Exception {
	    LoginRequestDTO invalidPasswordRequestDTO = LoginRequestDTO.builder()
	            .email(testEmail)
	            .password("wrongPassword")
	            .build();
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPasswordRequestDTO)))
                .andExpect(status().isUnauthorized());
    }
}