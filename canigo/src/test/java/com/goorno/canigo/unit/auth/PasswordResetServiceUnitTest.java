package com.goorno.canigo.unit.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.goorno.canigo.common.exception.ErrorCode;
import com.goorno.canigo.common.exception.UserException;
import com.goorno.canigo.dto.auth.PasswordResetConfirmDTO;
import com.goorno.canigo.entity.PasswordResetToken;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.PasswordResetTokenRepository;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.auth.PasswordResetService;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

class PasswordResetServiceUnitTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private UserRepository userRepository;
    
    @Mock 
    private PasswordResetTokenRepository tokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private PasswordResetToken validToken;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // mailUsername과 resetUrl 수동 주입
        ReflectionTestUtils.setField(passwordResetService, "mailUsername", "test@example.com");
        ReflectionTestUtils.setField(passwordResetService, "resetUrl", "http://localhost:3000/reset-password/");

        user = User.builder()
                .email("test@example.com")
                .nickname("tester")
                .password("password123")
                .authProvider(AuthProviderType.LOCAL)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .isVerified(true)
                .build();
        
        validToken = PasswordResetToken.builder()
                .token("valid-token")
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 발송 성공")
    void sendPasswordResetEmail_Success() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(templateEngine.process(eq("reset-password"), any(Context.class))).thenReturn("<html>Reset Link</html>");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // when
        assertDoesNotThrow(() -> passwordResetService.sendPasswordResetEmail(user.getEmail()));

        // then
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 발송 실패 - 존재하지 않는 사용자")
    void sendPasswordResetEmail_Fail_UserNotFound() {
        // given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // when + then
        UserException exception = assertThrows(UserException.class, () ->
                passwordResetService.sendPasswordResetEmail("notfound@example.com")
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPassword_Success() {
        // given
        PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("new-password");
        dto.setConfirmPassword("new-password");

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));
        when(passwordEncoder.matches("new-password", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        // when
        assertDoesNotThrow(() -> passwordResetService.resetPassword(dto));

        // then
        verify(userRepository).save(user);
        verify(tokenRepository).delete(validToken);
        assertEquals("encoded-new-password", user.getPassword());
    }
    
    @Test
    @DisplayName("비밀번호 재설정 실패 - 유효하지 않은 토큰")
    void resetPassword_Fail_InvalidToken() {
        // given
    	PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("invalid-token");
        dto.setNewPassword("new-password");
        dto.setConfirmPassword("new-password");

        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // when + then
        UserException exception = assertThrows(UserException.class, () -> passwordResetService.resetPassword(dto));
        assertEquals(ErrorCode.INVALID_PASSWORD_RESET_TOKEN, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("비밀번호 재설정 실패 - 토큰 만료")
    void resetPassword_Fail_ExpiredToken() {
        // given
        validToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("new-password");
        dto.setConfirmPassword("new-password");

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

        // when + then
        UserException exception = assertThrows(UserException.class, () -> passwordResetService.resetPassword(dto));
        assertEquals(ErrorCode.EXPIRED_PASSWORD_RESET_TOKEN, exception.getErrorCode());
        verify(tokenRepository).delete(validToken);
    }
    
    @Test
    @DisplayName("비밀번호 재설정 실패 - 비밀번호 불일치")
    void resetPassword_Fail_PasswordMismatch() {
        // given
        PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("new-password1");
        dto.setConfirmPassword("new-password2");

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

        // when + then
        UserException exception = assertThrows(UserException.class, () -> passwordResetService.resetPassword(dto));
        assertEquals(ErrorCode.PASSWORD_CONFIRM_MISMATCH, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("비밀번호 재설정 실패 - 비밀번호 너무 짧음")
    void resetPassword_Fail_TooShort() {
        // given
        PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("short");
        dto.setConfirmPassword("short");

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

        // when + then
        UserException exception = assertThrows(UserException.class, () -> passwordResetService.resetPassword(dto));
        assertEquals(ErrorCode.PASSWORD_TOO_SHORT, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("비밀번호 재설정 실패 - 기존 비밀번호와 동일")
    void resetPassword_Fail_DuplicatePassword() {
        // given
    	PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("same-password");
        dto.setConfirmPassword("same-password");

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));
        when(passwordEncoder.matches("same-password", user.getPassword())).thenReturn(true);

        // when + then
        UserException exception = assertThrows(UserException.class, () -> passwordResetService.resetPassword(dto));
        assertEquals(ErrorCode.PASSWORD_DUPLICATE, exception.getErrorCode());
    }
    
}
