package com.goorno.canigo.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.service.auth.LoginService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;

	@PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // 1. 로그인 서비스 호출
        LoginResponseDTO loginResponseDTO = loginService.login(loginRequestDTO);

        // 2. 클라이언트에 응답
        return ResponseEntity.ok(loginResponseDTO);
    }
}