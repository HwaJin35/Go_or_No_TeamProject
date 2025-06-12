package com.goorno.canigo.service.auth;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goorno.canigo.common.exception.ErrorCode;
import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.common.util.HashUtil;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.entity.RefreshToken;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.RefreshTokenRepository;
import com.goorno.canigo.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	
	@Transactional
	public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
		String email = loginRequestDTO.getEmail();

		// 이메일 존재 여부
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.AUTHENTICATION_USER_NOT_FOUND.getMessage()));

		// 계정 비활성화 상태 확인
		if (user.getStatus() != Status.ACTIVE) {
			throw new DisabledException(ErrorCode.ACCOUNT_DISABLED.getMessage());
		}

		// 비밀번호 검증
		authenticationManager
				.authenticate(
						new UsernamePasswordAuthenticationToken(email, loginRequestDTO.getPassword())
						);

		String accessToken = jwtUtil.createAccessToken(
				user.getId(),
				user.getEmail(),
				user.getNickname(),
				user.getRole().name()
				);
		
		
		String refreshToken = jwtUtil.createRefreshToken(
				user.getId(),
				user.getEmail()
				);
		
		// refreshToken 암호화 후 저장
		String hashedToken = HashUtil.sha256(refreshToken);
		refreshTokenRepository.deleteByUser_Id(user.getId());
		refreshTokenRepository.flush();
		refreshTokenRepository.save(new RefreshToken(
				hashedToken,
				user,
				LocalDateTime.now().plusDays(7)
			));
		// 쿠키에 평문 refreshToken 설정
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
			    .httpOnly(true)
			    .secure(false)
			    .path("/")
			    .maxAge(60*60*24*7)
			    .sameSite("Strict")
			    .build();
		System.out.println("Set-Cookie 헤더 추가됨: " + refreshCookie.toString());

		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	    
		return new LoginResponseDTO(accessToken, user.getNickname(), user.getRole().name());
	}
	
	@Transactional
	public ResponseEntity<String> logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Authorization 헤더가 유효하지 않습니다.");
        }
        String accessToken = authHeader.substring(7);
        long userId;
        try {
            // 토큰에서 userId 추출
            userId = jwtUtil.getUserIdFromClaim(accessToken);

           
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
        // 해당 user의 refreshToken 삭제
        refreshTokenRepository.deleteByUser_Id(userId);
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }
}