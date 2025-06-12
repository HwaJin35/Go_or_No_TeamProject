package com.goorno.canigo.integration.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.UserRepository;

import jakarta.servlet.http.Cookie;

// LoginController 통합테스트
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
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
	    
	    // 이메일 인증 코드 요청 (DB에 유저 생성됨)
	    mockMvc.perform(post("/api/auth/email/send")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("{\"email\": \"" + testEmail + "\"}"))
	        .andExpect(status().isOk());

	    // 인증 코드 세팅
	    User user = userRepository.findByEmail(testEmail)
	            .orElseThrow(() -> new RuntimeException("테스트 유저 생성 실패"));
	    
	    user.setAuthToken("123456");
	    user.setAuthTokenExpiresAt(LocalDateTime.now().plusMinutes(5));
	    userRepository.save(user);

	    // 이메일 인증
	    mockMvc.perform(post("/api/auth/email/verify")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("{\"email\": \"" + testEmail + "\", \"code\": \"123456\"}"))
	        .andExpect(status().isOk());
	    
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
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(result -> {
            String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
            assertThat(setCookieHeader).isNotNull();
            assertThat(setCookieHeader).contains("refreshToken=");
            assertThat(setCookieHeader).contains("HttpOnly");
        });
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
	@Test
	@DisplayName("리프레시 토큰을 이용한 AccessToken 재발급 성공")
	void refresh_accessToken_success() throws Exception {
	    // 로그인 먼저 수행
	    MvcResult loginResult = mockMvc.perform(post("/api/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(loginRequestDTO)))
	        .andExpect(status().isOk())
	        .andReturn();

	    String refreshTokenCookie = loginResult.getResponse().getHeader("Set-Cookie");

	    mockMvc.perform(post("/api/auth/refresh")
	            .cookie(new Cookie("refreshToken", extractTokenValue(refreshTokenCookie))))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.accessToken").exists());
	}

	// 쿠키값에서 token만 추출하는 유틸
	private String extractTokenValue(String setCookieHeader) {
	    return setCookieHeader.split(";")[0].split("=")[1];
	}
}