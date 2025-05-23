package com.goorno.canigo.common.jwt;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.common.response.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 인가(Authorization) 실패 시 호출되는 핸들러
// 권한이 없는 사용자가 보호된 리소스에 접근할 때 호출됨 (403 Forbidden)
// 관리자 페이지 구현 시 사용
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	
	// ObjectMapper는 객체를 JSON 문자열로 변환해주는 Jackson 유틸리티
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request,
					   HttpServletResponse response,
					   AccessDeniedException accessDeniedException)
			throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
		
		// 응답 헤더 설정 (JSON 형식, UTF-8 인코딩)
		response.setContentType("application/json;charset=UTF-8");
		
		// 에러 응답 객체 생성 (ErrorResponse 형식에 맞춤)
		ErrorResponse error = new ErrorResponse(
			403,
			"FORBIDDEN",
			"권한이 없습니다.",
			request.getRequestURI()
		);
		
		// ErrorResponse 객체를 JSON 문자열로 변환 후 응답 본문에 작성
		String json = objectMapper.writeValueAsString(error);
		response.getWriter().write(json);
	}
}