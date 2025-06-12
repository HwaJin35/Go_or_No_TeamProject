package com.goorno.canigo.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.service.auth.LoginService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
	    LoginResponseDTO loginResponseDTO = loginService.login(loginRequestDTO, response);
	    return ResponseEntity.ok(loginResponseDTO);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(
	    @RequestHeader("Authorization") String authHeader,
	    HttpServletResponse response
	) {
	    ResponseEntity<String> result = loginService.logout(authHeader);

	    // 쿠키 제거: 같은 이름, 경로, MaxAge 0으로 설정
	    ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
	        .httpOnly(true)
	        .secure(false) // 개발환경에서는 false
	        .path("/")
	        .maxAge(0) // 즉시 만료
	        .build();

	    return ResponseEntity
	        .status(result.getStatusCode())
	        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
	        .body(result.getBody());
	}
}