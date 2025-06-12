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
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.common.jwt.JwtAuthenticationFilter;
import com.goorno.canigo.controller.auth.LoginController;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.service.auth.LoginService;

import jakarta.servlet.http.HttpServletResponse;

// LoginController 단위테스트
@ActiveProfiles("test")
@WebMvcTest(
    controllers = LoginController.class, // 이 컨트롤러만 스캔
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class // 스프링 시큐리티 자동 구성 제외
    },
    // JwtAuthenticationFilter를 ComponentScan에서 제외
    // JwtAuthenticationFilter가 @Component, @Configuration 등으로 빈 등록될 경우,
    // @WebMvcTest의 스캔 범위에 포함될 수 있기 때문.
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class // JwtAuthenticationFilter 클래스 자체를 제외
    )
)
@AutoConfigureMockMvc
public class LoginControllerUnitTest {
	
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
		when(loginService.login(any(), any(HttpServletResponse.class))).thenReturn(new LoginResponseDTO("fake-jwt-token","fake-nickname","USER"));
		
		mockMvc.perform(post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("fake-jwt-token"));
	}
	
	@Test
	@DisplayName("로그인 실패(이메일 없음) 테스트")
	void login_fail_emailNotFound() throws Exception {
		when(loginService.login(any(), any(HttpServletResponse.class))).thenThrow(new IllegalArgumentException("비밀번호가 일치하지 않습니다."));
		
		mockMvc.perform(post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDTO)))
				.andExpect(status().isBadRequest());
	}

}
