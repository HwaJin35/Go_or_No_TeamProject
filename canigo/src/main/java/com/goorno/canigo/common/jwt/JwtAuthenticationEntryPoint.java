package com.goorno.canigo.common.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됨
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	 @Override
	    public void commence(HttpServletRequest request,
	                         HttpServletResponse response,
	                         AuthenticationException authException) throws IOException, ServletException {
	        // 401 Unauthorized 상태 코드 응답
	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
	    }
}
