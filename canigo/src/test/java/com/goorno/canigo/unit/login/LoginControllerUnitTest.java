package com.goorno.canigo.unit.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.controller.login.LoginController;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.service.login.LoginService;

// LoginController 단위테스트
@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters=false)	// 테스트를 위해 시큐리티 필터 제거
public class LoginControllerUnitTest {
	// JPA Auditing 기능을 위한 AuditorAware 가짜 객체 등록
	@TestConfiguration
	static class TestConfig {
		@Bean
		public AuditorAware<String> auditorAware() {
			return () -> java.util.Optional.of("testUser");
		}
	}
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	// 단위테스트에서 불필요한 JPA 관련 설정 로딩을 방지하기 위해 Mock 등록
	@MockBean
	JpaMetamodelMappingContext jpaMetamodelMappingContext;	
	
	@MockBean
	private LoginService loginService;
	
	private LoginRequestDTO loginRequestDTO;
	
	@BeforeEach
	void setUp() {
	loginRequestDTO = LoginRequestDTO.builder()
			.email("test@example.com")
			.password("testPassword")
			.build();
	}
	
	@Test
	@DisplayName("로그인 성공 테스트")
	void login_success() throws Exception {		
		when(loginService.login(any())).thenReturn(new LoginResponseDTO("fake-jwt-token", "fake-nickname","USER"));
		
		mockMvc.perform(post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("fake-jwt-token"));
	}
	
	@Test
	@DisplayName("로그인 실패(이메일 없음) 테스트")
	void login_fail_emailNotFound() throws Exception {
		when(loginService.login(any())).thenThrow(new IllegalArgumentException("비밀번호가 일치하지 않습니다."));
		
		mockMvc.perform(post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDTO)))
				.andExpect(status().isBadRequest());
	}

}
