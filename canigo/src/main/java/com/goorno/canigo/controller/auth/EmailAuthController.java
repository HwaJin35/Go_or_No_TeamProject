package com.goorno.canigo.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.service.auth.EmailAuthService;

import lombok.RequiredArgsConstructor;

// 이메일 인증 코드 발송, 메일 발송, 인증코드 검증 및 완료처리

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailAuthController {
	
	private final EmailAuthService emailAuthService;
	
	// 이메일 인증코드 요청 API
	@PostMapping("/email/send")
	public ResponseEntity<String> sendAuthCode(@RequestBody EmailAuthRequestDTO requestDTO) {
		emailAuthService.sendVerificationCode(requestDTO);
		return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
	}
	
	// 이메일 인증코드 검증 API
	@PostMapping("/email/verify")
	public ResponseEntity<String> verifyAuthCode(@RequestBody EmailVerifyDTO verifyDTO){
		emailAuthService.verityCode(verifyDTO);
		return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
	}

}
