package com.goorno.canigo.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.dto.auth.PasswordResetConfirmDTO;
import com.goorno.canigo.dto.auth.PasswordResetRequestDTO;
import com.goorno.canigo.service.auth.AuthService;
import com.goorno.canigo.service.auth.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// 이메일 인증 코드 발송, 메일 발송, 인증코드 검증 및 완료처리

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService emailAuthService;
	private final PasswordResetService passwordResetService;
	
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
	
	// 비밀번호 재설정 이메일 발송 API
	@PostMapping("/reset-password/send")
	public ResponseEntity<?> sendResetPassword(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
	    passwordResetService.sendPasswordResetEmail(requestDTO.getEmail());
	    return ResponseEntity.ok("비밀번호 재설정 링크가 이메일로 발송되었습니다.");
	}
	
	// 비밀번호 재설정 처리 API
	    @PostMapping("/reset-password/reset")
	    public ResponseEntity<?> confirmResetPassword(@Valid @RequestBody PasswordResetConfirmDTO confirmDTO) {
	        passwordResetService.resetPassword(confirmDTO);
	        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	    }
}
