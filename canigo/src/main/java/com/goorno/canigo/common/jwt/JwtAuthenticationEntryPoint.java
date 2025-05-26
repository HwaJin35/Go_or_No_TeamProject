package com.goorno.canigo.common.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.common.response.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됨
// 즉, SecurityContext에 인증 정보(Authentication)가 없는 상태에서 접근하는 경우.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환 도구

	@Override
	public void commence(HttpServletRequest request,
	                     HttpServletResponse response,
	                     AuthenticationException authException)
			throws IOException, ServletException {

		// HTTP 상태 코드 401 Unauthorized 설정
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// 응답 Content-Type 지정 (JSON)
		response.setContentType("application/json;charset=UTF-8");

		// ErrorResponse 객체 생성 (전역 에러 응답 포맷과 일치시킴)
		ErrorResponse error = new ErrorResponse(
			401,								// 상태 코드
			"UNAUTHORIZED",						// 에러 요약
			"인증이 필요합니다.",				// 메시지
			request.getRequestURI()				// 요청 경로
		);

		// ErrorResponse를 JSON 문자열로 변환 후 출력
		String json = objectMapper.writeValueAsString(error);
		response.getWriter().write(json);
	}
}