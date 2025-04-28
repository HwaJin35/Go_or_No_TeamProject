package com.goorno.canigo.unit.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.login.LoginServiceImpl;

//LoginService 단위 테스트
@ExtendWith(MockitoExtension.class)
public class LoginServiceUnitTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtUtil jwtUtil;
	
	@InjectMocks
	private LoginServiceImpl loginService;
	
	private LoginRequestDTO loginRequestDTO;
	private User user;
	
	@BeforeEach
	void setUp() {
		loginRequestDTO = LoginRequestDTO.builder()
				.email("test@example.com")
				.password("testPassword")
				.build();
		
		user = User.builder()
				.id(1L)
				.email("test@example.com")
				.password("encodedPassword")
				.nickname("tester")
				.build();
	}
	@Test
    @DisplayName("로그인 성공")
    void login_success() {
        when(userRepository.findByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(any(), any(), any())).thenReturn("fake-jwt-token");

        LoginResponseDTO result = loginService.login(loginRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("fake-jwt-token");
    }

    @Test
    @DisplayName("이메일 존재하지 않을 때 로그인 실패")
    void login_fail_emailNotFound() {
    	// 이메일이 존재하지 않는 상황 가정
        when(userRepository.findByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.empty());
        // assertThatThrownBy : 특정 코드 실행 시 예외가 발생하는지 검증
        assertThatThrownBy(() -> loginService.login(loginRequestDTO))	// 코드 실행 시
                .isInstanceOf(BadCredentialsException.class)			// IllegalArgumentException 예외가 발생하고
                .hasMessageContaining("존재하지 않는 이메일입니다.");	// 예외 메시지에 해당 문구가 포함되는지 검증
    }

    @Test
    @DisplayName("비밀번호 불일치로 로그인 실패")
    void login_fail_invalidPassword() {
    	// 이메일은 존재하나, 비밀번호가 일치하지 않는 상황 가정
        when(userRepository.findByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(user));	
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> loginService.login(loginRequestDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }
}
