package com.goorno.canigo.service.login;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
	
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new BadCredentialsException("존재하지 않는 이메일입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 3. Access Token 발급
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), "USER");

        // 4. 응답 DTO에 담아서 반환
        return new LoginResponseDTO(accessToken);
    }
}