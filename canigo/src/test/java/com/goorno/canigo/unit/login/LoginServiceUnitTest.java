package com.goorno.canigo.unit.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.RefreshTokenRepository;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.auth.LoginService;

import jakarta.servlet.http.HttpServletResponse;

//LoginService 단위 테스트
@ExtendWith(MockitoExtension.class)
public class LoginServiceUnitTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
    private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private LoginService loginService;
	private LoginRequestDTO loginRequestDTO;
	private User user;

	@BeforeEach
	void setUp() {
		loginRequestDTO = LoginRequestDTO.builder().email("test@example.com").password("testPassword").build();

		user = User.builder().id(1L).email("test@example.com").password("encodedPassword").nickname("tester")
				.role(Role.USER).status(Status.ACTIVE).build();
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(userRepository.findByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(user));

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(mock(Authentication.class));

		when(jwtUtil.createAccessToken(any(), any(), any(), any())).thenReturn("fake-jwt-token");
		when(jwtUtil.createRefreshToken(any(), any())).thenReturn("fake-refresh-token");
		// hashed refreshToken 처리
	    LoginResponseDTO result = loginService.login(loginRequestDTO, response);

		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).isEqualTo("fake-jwt-token");
		assertThat(result.getNickname()).isEqualTo("tester");
	}

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void login_fail_emailNotFound() {
    	HttpServletResponse response = mock(HttpServletResponse.class);
        when(userRepository.findByEmail(loginRequestDTO.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login(loginRequestDTO, response))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

	@Test
    @DisplayName("비활성 계정으로 로그인 실패")
    void login_fail_accountDisabled() {
		HttpServletResponse response = mock(HttpServletResponse.class);
        User disabledUser = User.builder()
                .id(2L)
                .email("disabled@example.com")
                .password("pw")
                .nickname("test")
                .role(Role.USER)
                .status(Status.PENDING) // 또는 Status.DELETED
                .build();

        when(userRepository.findByEmail(disabledUser.getEmail()))
                .thenReturn(Optional.of(disabledUser));

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email(disabledUser.getEmail())
                .password("pw")
                .build();

        assertThatThrownBy(() -> loginService.login(request, response))
                .isInstanceOf(DisabledException.class)
                .hasMessageContaining("비활성화된 계정입니다.");
    }
	
	@Test
    @DisplayName("비밀번호 불일치로 로그인 실패")
    void login_fail_invalidPassword() {
		HttpServletResponse response = mock(HttpServletResponse.class);
        when(userRepository.findByEmail(loginRequestDTO.getEmail()))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("비밀번호가 일치하지 않습니다."));

        assertThatThrownBy(() -> loginService.login(loginRequestDTO, response))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }
}
