package com.goorno.canigo.unit.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // 요청 빌더
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // 응답 내용 검증
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;   // HTTP 상태 코드 검증

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.common.jwt.JwtAuthenticationFilter;
import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.controller.auth.AuthController;
import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.dto.auth.PasswordResetConfirmDTO;
import com.goorno.canigo.service.auth.EmailAuthService;
import com.goorno.canigo.service.auth.PasswordResetService;
import com.goorno.canigo.service.auth.RefreshTokenService;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = AuthController.class,
    // SecurityAutoConfiguration을 제외합니다.
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class // 스프링 시큐리티 자동 구성 제외
    }
)
@AutoConfigureMockMvc
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private EmailAuthService emailAuthService;
    
    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private JwtUtil jwtUtil;

     @MockBean
     private JwtAuthenticationFilter jwtAuthenticationFilter;
     
     @MockBean
     private UserDetailsService userDetailsService;
     
     @MockBean
     private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("이메일 인증 코드 발송 - 성공")
    void sendAuthCode() throws Exception {
        // given
        EmailAuthRequestDTO requestDTO = new EmailAuthRequestDTO("test@example.com");
        
        // Mokito의 doNothing()은 void 메서드에 사용
        doNothing().when(emailAuthService).sendVerificationCode(requestDTO);

        // when & then
        mockMvc.perform(post("/api/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 - 성공")
    void verifyAuthCode() throws Exception {
        // given
        EmailVerifyDTO verifyDTO = new EmailVerifyDTO("test@example.com", "123456");

        // any()를 사용하여 어떤 EmailVerifyDTO 객체가 와도 doNothing() 적용
        doNothing().when(emailAuthService).verifyCode(any(EmailVerifyDTO.class));

        // when & then
        mockMvc.perform(post("/api/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
    
    @Test
    @DisplayName("비밀번호 재설정 이메일 발송 - 성공")
    void sendResetPasswordEmail() throws Exception {
        String email = "test@example.com";
        doNothing().when(passwordResetService).sendPasswordResetEmail(email);

        mockMvc.perform(post("/api/auth/reset-password/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
    
    @Test
    @DisplayName("비밀번호 재설정 완료 - 성공")
    void resetPassword() throws Exception {
    	// given
        PasswordResetConfirmDTO dto = new PasswordResetConfirmDTO();
        dto.setToken("reset-token-123");
        dto.setNewPassword("newSecure123!");
        dto.setConfirmPassword("newSecure123!");

        doNothing().when(passwordResetService).resetPassword(any(PasswordResetConfirmDTO.class));
        // when & then
        mockMvc.perform(post("/api/auth/reset-password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
    
}