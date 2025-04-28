package com.goorno.canigo.integration.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.goorno.canigo.dto.login.LoginResponseDTO;

// 회원정보 조회 통합 테스트
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProfileIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String testEmail;
	private String testPassword;
	private String testNickname;
	private MockMultipartFile profileImage;
	private String accessToken;

	@BeforeEach
	void setUp() throws Exception {
		// 테스트용 데이터 준비
		testEmail = "test" + System.currentTimeMillis() + "@example.com";
		testPassword = "testPassword123";
		testNickname = "tester" + System.currentTimeMillis();

		profileImage = new MockMultipartFile("files", "test.jpg", "image/jpeg",
				"test image content".getBytes(StandardCharsets.UTF_8));

		// 회원가입 먼저 수행
		mockMvc.perform(multipart("/api/users/signup").file(profileImage).param("email", testEmail)
				.param("password", testPassword).param("nickname", testNickname).param("authProvider", "LOCAL"))
				.andExpect(status().isOk());

		// 로그인하여 accessToken 획득
		LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder().email(testEmail).password(testPassword).build();

		String responseBody = mockMvc
				.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequestDTO)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		LoginResponseDTO loginResponse = objectMapper.readValue(responseBody, LoginResponseDTO.class);
		accessToken = loginResponse.getAccessToken();
	}

	@Test
	@DisplayName("회원정보 조회 성공 - 유효한 토큰")
	void getMyProfile_success() throws Exception {
		mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.email").value(testEmail))
				.andExpect(jsonPath("$.nickname").value(testNickname));
	}

	@Test
	@DisplayName("회원정보 조회 실패 - 토큰 없음")
	void getMyProfile_fail_noToken() throws Exception {
		mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("회원정보 조회 실패 - 잘못된 토큰")
	void getMyProfile_fail_invalidToken() throws Exception {
		mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer wrong.token.here"))
				.andExpect(status().isUnauthorized());
	}
}