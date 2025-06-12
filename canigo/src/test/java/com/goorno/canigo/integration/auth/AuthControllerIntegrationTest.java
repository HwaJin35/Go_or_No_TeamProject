package com.goorno.canigo.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.dto.auth.PasswordResetRequestDTO;
import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.PasswordResetTokenRepository;
import com.goorno.canigo.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.goorno.canigo.repository")
@EntityScan(basePackages = "com.goorno.canigo.entity")       
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
    	passwordResetTokenRepository.deleteAll();
        userRepository.deleteAll(); 
        userRepository.save(User.builder()
                .email("test@example.com")
                .nickname("tester")
                .password(passwordEncoder.encode("password123"))
                .authProvider(AuthProviderType.LOCAL)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .isVerified(false)
                .build());
    }

    @Test
    @WithMockUser	// 가짜 인증 추가
    @DisplayName("이메일 인증 코드 발송 - 통합테스트 성공")
    void sendAuthCode() throws Exception {
    	MimeMessage fakeMimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(fakeMimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        EmailAuthRequestDTO requestDTO = new EmailAuthRequestDTO("test@example.com");

        mockMvc.perform(post("/api/auth/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 이메일로 발송되었습니다."));
    }

    @Test
    @WithMockUser // 가짜 인증 추가
    @DisplayName("이메일 인증 코드 검증 - 통합테스트 성공")
    void verifyAuthCode() throws Exception {
    	MimeMessage fakeMimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(fakeMimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        EmailAuthRequestDTO sendDTO = new EmailAuthRequestDTO("test@example.com");

        mockMvc.perform(post("/api/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendDTO)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        String authCode = user.getAuthToken();

        EmailVerifyDTO verifyDTO = new EmailVerifyDTO("test@example.com", authCode);

        mockMvc.perform(post("/api/auth/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("이메일 인증이 완료되었습니다."));
    }
    
    @Test
    @WithMockUser
    @DisplayName("비밀번호 재설정 메일 발송 - 통합테스트 성공")
    void sendResetPasswordMail() throws Exception {
    	MimeMessage fakeMimeMessage = mock(MimeMessage.class);
    	when(javaMailSender.createMimeMessage()).thenReturn(fakeMimeMessage);
    	doNothing().when(javaMailSender).send(any(MimeMessage.class));
    	
    	PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO();
    	requestDTO.setEmail("test@example.com");
    	userRepository.flush();
    	
    	mockMvc.perform(post("/api/auth/reset-password/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk())
        .andExpect(content().string("비밀번호 재설정 링크가 이메일로 발송되었습니다."));
    }
    @Test
    @DisplayName("로그인 시 accessToken과 Set-Cookie 헤더 확인")
    void login_with_tokens() throws Exception {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(AuthProviderType.LOCAL,"test@example.com", "password123");

        // when
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(result -> {
                String cookie = result.getResponse().getHeader("Set-Cookie");
                assertThat(cookie).contains("refreshToken=");
            });
    }
}