package com.goorno.canigo.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.goorno.canigo.common.exception.ErrorCode;
import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
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
				.authenticate(new UsernamePasswordAuthenticationToken(email, loginRequestDTO.getPassword()));

		String token = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
		return new LoginResponseDTO(token, user.getNickname(), user.getRole().name());
	}
}