package com.goorno.canigo.integration.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailAuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    
    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); 
        userRepository.save(User.builder()
                .email("test@example.com")
                .nickname("tester")
                .password("password123")
                .authProvider(AuthProviderType.LOCAL)
                .role(Role.ROLE_USER)
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
}