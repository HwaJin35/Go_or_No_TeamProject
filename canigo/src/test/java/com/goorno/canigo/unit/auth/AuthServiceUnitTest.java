package com.goorno.canigo.unit.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.auth.EmailAuthService;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

class AuthServiceUnitTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailAuthService emailAuthService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .email("test@example.com")
                .nickname("tester")
                .password("password123")
                .authProvider(AuthProviderType.LOCAL)
                .isVerified(false)
                .build();
    }

    @Test
    @DisplayName("인증 코드 이메일 발송 성공")
    void sendVerificationCode_Success() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>dummy</html>");

        // when
        emailAuthService.sendVerificationCode(new EmailAuthRequestDTO(user.getEmail()));

        // then
        verify(userRepository, times(1)).save(user);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void verifyCode_Success() {
        // given
        String authCode = String.format("%06d", new Random().nextInt(999999));
        user.setAuthToken(authCode);
        user.setAuthTokenExpiresAt(LocalDateTime.now().plusMinutes(3));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when
        emailAuthService.verifyCode(new EmailVerifyDTO(user.getEmail(), authCode));

        // then
        assertTrue(user.isVerified());
        assertNull(user.getAuthToken());
        assertNull(user.getAuthTokenExpiresAt());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("이메일 인증 실패 - 코드 불일치")
    void verifyCode_Fail_WrongCode() {
        // given
        user.setAuthToken("654321");
        user.setAuthTokenExpiresAt(LocalDateTime.now().plusMinutes(3));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailAuthService.verifyCode(new EmailVerifyDTO(user.getEmail(), "123456"));
        });

        assertEquals("인증 코드가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 코드 만료")
    void verifyCode_Fail_ExpiredCode() {
        // given
        user.setAuthToken("123456");
        user.setAuthTokenExpiresAt(LocalDateTime.now().minusMinutes(1)); // 만료된 시간

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when + then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            emailAuthService.verifyCode(new EmailVerifyDTO(user.getEmail(), "123456"));
        });

        assertEquals("인증 코드가 만료되었습니다. 다시 요청해 주세요", exception.getMessage());
    }
}